package com.rebalance.service;

import com.rebalance.entity.*;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.ExpenseRepository;
import com.rebalance.repository.ExpenseUsersRepository;
import com.rebalance.repository.UserGroupRepository;
import com.rebalance.security.SignedInUsernameGetter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final NotificationService notificationService;
    private final CategoryService categoryService;
    private final ExpenseUsersRepository expenseUsersRepository;
    private final UserGroupRepository userGroupRepository;
    private final SignedInUsernameGetter signedInUsernameGetter;

    @Transactional
    public Expense saveGroupExpense(Expense expense, List<ExpenseUsers> expenseUsers, String category) {
        // validate input data
        validateUsersAmount(expense.getAmount(), expenseUsers);
        groupService.validateGroupExistsAndNotPersonal(expense.getGroup().getId());

        // validate users and initiator in group
        validateUsersInGroup(expenseUsers, expense.getInitiator(), expense.getGroup().getId());

        // set auto generated fields
        expense.setAddedBy(signedInUsernameGetter.getUser());
        if (expense.getDate() == null) {
            expense.setDate(LocalDateTime.now());
        }
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, expense.getGroup()));
        expenseRepository.save(expense);

        // save participants of expense
        expenseUsers.forEach(u -> u.setExpense(expense));
        expenseUsers.forEach(u -> {
            if (u.getMultiplier() == null) u.setMultiplier(1);
        });
        expenseUsersRepository.saveAll(expenseUsers);

        // update users balances in group
        HashMap<Long, Double> userChanges = getBalanceDiff(expenseUsers, expense.getInitiator().getId(), expense.getAmount());
        updateUsersBalanceInGroup(userChanges, expense.getGroup().getId());

        // set expense participants for response
        expense.setExpenseUsers(new HashSet<>(expenseUsers));
        return expense;
    }

    @Transactional
    public Expense editGroupExpense(Expense expenseRequest, List<ExpenseUsers> expenseUsers, String category) {
        // get existing expense
        Expense expense = getExpenseById(expenseRequest.getId());

        // validate input data
        validateUsersAmount(expenseRequest.getAmount(), expenseUsers);
        groupService.validateGroupIsNotPersonal(expense.getGroup());

        // validate users and initiator in group
        validateUsersInGroup(expenseUsers, expenseRequest.getInitiator(), expense.getGroup().getId());

        Long oldInitiatorId = expense.getInitiator().getId();
        Double oldAmount = expense.getAmount();

        // update fields
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, expense.getGroup()));
        expenseRepository.save(expense);

        // update users balances in group
        List<ExpenseUsers> oldExpenseUsers = expenseUsersRepository.findAllByExpenseId(expense.getId());

        // add old expenses difference and old initiator difference
        HashMap<Long, Double> userChanges = getInverseBalanceDiff(oldExpenseUsers, oldInitiatorId, oldAmount);
        // add new expenses difference and new initiator difference
        HashMap<Long, Double> newUserChanges = getBalanceDiff(expenseUsers, expense.getInitiator().getId(), expense.getAmount());
        for (Long key : newUserChanges.keySet()) {
            if (userChanges.containsKey(key)) {
                userChanges.put(key, userChanges.get(key) + newUserChanges.get(key));
            } else {
                userChanges.put(key, newUserChanges.get(key));
            }
        }
        updateUsersBalanceInGroup(userChanges, expense.getGroup().getId());

        // update users
        expenseUsersRepository.deleteAllByExpenseId(expense.getId());
        expenseUsers.forEach(u -> u.setExpense(expense));
        expenseUsersRepository.saveAll(expenseUsers);
        expense.setExpenseUsers(new HashSet<>(expenseUsers));

        return expense;
    }

    public Expense savePersonalExpense(Expense expense, String category) {
        User signedInUser = signedInUsernameGetter.getUser();
        Group group = groupService.getPersonalGroupByUserId(signedInUser.getId());

        expense.setInitiator(signedInUser);
        expense.setAddedBy(signedInUser);
        expense.setGroup(group);
        if (expense.getDate() == null) {
            expense.setDate(LocalDateTime.now());
        }
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, group));
        return expenseRepository.save(expense);
    }

    public Expense editPersonalExpense(Expense expenseRequest, String category) {
        // get existing expense
        Expense expense = getExpenseById(expenseRequest.getId());

        User signedInUser = signedInUsernameGetter.getUser();

        // validate expense is user's personal
        if (!expense.getGroup().isPersonalOf(signedInUser)) {
            throw new RebalanceException(RebalanceErrorType.RB_204);
        }

        // update fields
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, expense.getGroup()));
        return expenseRepository.save(expense);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_101));
    }

    public void deleteGroupExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        groupService.validateGroupIsNotPersonal(expense.getGroup());

        // update users balances in group
        List<ExpenseUsers> expenseUsers = expenseUsersRepository.findAllByExpenseId(expense.getId());

        HashMap<Long, Double> userChanges = getInverseBalanceDiff(expenseUsers, expense.getInitiator().getId(), expense.getAmount());
        updateUsersBalanceInGroup(userChanges, expense.getGroup().getId());

        expenseRepository.deleteById(expenseId);
    }

    public void deletePersonalExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        User signedInUser = signedInUsernameGetter.getUser();
        groupService.validateGroupIsPersonal(expense.getGroup().getId(), signedInUser.getId());

        expenseRepository.deleteById(expenseId);
    }

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId) {
        if (expenseRepository.findById(globalId).isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
    }

    public Page<Expense> getExpensesOfGroup(Long groupId, Integer page, Integer size) {
        groupService.validateGroupExistsAndNotPersonal(groupId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return expenseRepository.findAllByGroupId(groupId, pageable);
    }

    public Page<Expense> getExpensesOfUser(Integer page, Integer size) {
        User signedInuser = signedInUsernameGetter.getUser();
        Group personalGroup = groupService.getPersonalGroupByUserId(signedInuser.getId());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return expenseRepository.findAllByGroupId(personalGroup.getId(), pageable);
    }

    private HashMap<Long, Double> getBalanceDiff(List<ExpenseUsers> expenseUsers, Long initiatorId,
                                                 Double expenseAmount) {
        HashMap<Long, Double> userChanges = new HashMap<>(expenseUsers.size() + 1);
        expenseUsers.forEach(u -> userChanges.put(u.getUser().getId(), -u.getAmount()));
        if (userChanges.containsKey(initiatorId)) {
            userChanges.put(initiatorId, expenseAmount + userChanges.get(initiatorId));
        } else {
            userChanges.put(initiatorId, expenseAmount);
        }
        return userChanges;
    }

    private HashMap<Long, Double> getInverseBalanceDiff(List<ExpenseUsers> expenseUsers, Long initiatorId,
                                                        Double expenseAmount) {
        HashMap<Long, Double> userChanges = new HashMap<>(expenseUsers.size() + 1);
        expenseUsers.forEach(u -> userChanges.put(u.getUser().getId(), u.getAmount()));
        if (userChanges.containsKey(initiatorId)) {
            userChanges.put(initiatorId, -expenseAmount + userChanges.get(initiatorId));
        } else {
            userChanges.put(initiatorId, -expenseAmount);
        }
        return userChanges;
    }

    private void updateUsersBalanceInGroup(Map<Long, Double> userChanges, Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupIdAndUserIdIn(groupId, userChanges.keySet());
        userGroups.forEach(ug -> {
            ug.setBalance(ug.getBalance() + userChanges.get(ug.getUser().getId()));
        });
        userGroupRepository.saveAll(userGroups);
    }

    private void validateUsersAmount(Double amount, List<ExpenseUsers> expenseUsers) {
        if (expenseUsers.stream().mapToDouble(ExpenseUsers::getAmount).sum() != amount) {
            throw new RebalanceException(RebalanceErrorType.RB_104);
        }
    }

    private void validateUsersInGroup(List<ExpenseUsers> users, User initiator, Long groupId) {
        Set<Long> userIds = users.stream().map(expenseUser ->
                expenseUser.getUser().getId()).collect(Collectors.toSet());
        userIds.add(initiator.getId());
        groupService.validateUsersInGroup(userIds, groupId);
    }
}
