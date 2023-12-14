package com.rebalance.service;

import com.rebalance.entity.*;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.ExpenseRepository;
import com.rebalance.repository.ExpenseUsersRepository;
import com.rebalance.repository.UserGroupRepository;
import com.rebalance.repository.UserRepository;
import com.rebalance.security.SignedInUsernameGetter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final UserRepository userRepository;

    @Transactional
    public Expense saveGroupExpense(Expense expense, List<ExpenseUsers> expenseUsers, String category) {
        // validate input data
        validateUserUniqueness(expenseUsers);
        Group group = groupService.getNotPersonalGroupById(expense.getGroup().getId());

        User signedInUser = signedInUsernameGetter.getUser();
        // validate users, initiator and signed user in group
        validateUsersInGroup(expenseUsers, expense.getInitiator(), signedInUser, expense.getGroup().getId());

        // set auto generated fields
        expense.setAddedBy(signedInUser);
        if (expense.getDate() == null) {
            expense.setDate(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("UTC")));
        }
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, expense.getGroup()));
        expenseRepository.save(expense);

        // save participants of expense
        // calculate total multipliers
        BigDecimal totalMultipliers = BigDecimal.ZERO;
        for (ExpenseUsers eu : expenseUsers) {
            totalMultipliers = totalMultipliers.add(BigDecimal.valueOf(eu.getMultiplier()));
        }
        // set amount and expense for each ExpenseUsers
        for (ExpenseUsers eu : expenseUsers) {
            eu.setAmount(expense.getAmount().multiply(BigDecimal.valueOf(eu.getMultiplier()).divide(totalMultipliers, 100, RoundingMode.HALF_EVEN)));
            eu.setExpense(expense);
        }
        expenseUsersRepository.saveAll(expenseUsers);

        // update users balances in group
        HashMap<Long, BigDecimal> userChanges = getBalanceDiff(expenseUsers, expense.getInitiator().getId(), expense.getAmount());
        updateUsersBalanceInGroup(userChanges, expense.getGroup().getId());

        List<User> usersToBeNotified = userRepository.findAllById(expenseUsers.stream().map(eu -> eu.getUser().getId()).toList());
        usersToBeNotified.add(userRepository.findById(expense.getInitiator().getId()).get());
        notificationService.saveNotificationGroupExpense(signedInUser, expense, group, usersToBeNotified, NotificationType.GroupExpenseAdded);

        // set expense participants for response
        expense.setExpenseUsers(new HashSet<>(expenseUsers));
        return expense;
    }

    @Transactional
    public Expense editGroupExpense(Expense expenseRequest, List<ExpenseUsers> expenseUsers, String category) {
        // get existing expense
        Expense expense = getExpenseById(expenseRequest.getId());

        // validate input data
        validateUserUniqueness(expenseUsers);
        Group group = groupService.getNotPersonalGroupById(expense.getGroup().getId());

        User signedInUser = signedInUsernameGetter.getUser();
        // validate users and initiator in group
        validateUsersInGroup(expenseUsers, expenseRequest.getInitiator(), signedInUser, expense.getGroup().getId());

        Long oldInitiatorId = expense.getInitiator().getId();
        BigDecimal oldAmount = expense.getAmount();

        // update fields
        expense.setInitiator(expenseRequest.getInitiator());
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, expense.getGroup()));
        if (expenseRequest.getDate() != null) {
            expense.setDate(expenseRequest.getDate());
        }
        expenseRepository.save(expense);

        // calculate total multipliers
        BigDecimal totalMultipliers = BigDecimal.ZERO;
        for (ExpenseUsers eu : expenseUsers) {
            totalMultipliers = totalMultipliers.add(BigDecimal.valueOf(eu.getMultiplier()));
        }
        // set amount and expense for each ExpenseUsers
        for (ExpenseUsers eu : expenseUsers) {
            eu.setAmount(expense.getAmount().multiply(BigDecimal.valueOf(eu.getMultiplier()).divide(totalMultipliers, 100, RoundingMode.HALF_EVEN)));
            eu.setExpense(expense);
        }

        // update users balances in group
        List<ExpenseUsers> oldExpenseUsers = expenseUsersRepository.findAllByExpenseId(expense.getId());
        // add old expenses difference and old initiator difference
        HashMap<Long, BigDecimal> userChanges = getInverseBalanceDiff(oldExpenseUsers, oldInitiatorId, oldAmount);
        // add new expenses difference and new initiator difference
        HashMap<Long, BigDecimal> newUserChanges = getBalanceDiff(expenseUsers, expense.getInitiator().getId(), expense.getAmount());
        for (Long key : newUserChanges.keySet()) {
            if (userChanges.containsKey(key)) {
                userChanges.put(key, userChanges.get(key).add(newUserChanges.get(key)));
            } else {
                userChanges.put(key, newUserChanges.get(key));
            }
        }
        updateUsersBalanceInGroup(userChanges, expense.getGroup().getId());

        // update users
        expenseUsersRepository.deleteAllByExpenseId(expense.getId());
        expenseUsersRepository.saveAll(expenseUsers);
        expense.setExpenseUsers(new HashSet<>(expenseUsers));

        List<User> usersToBeNotified = userRepository.findAllById(expenseUsers.stream().map(eu -> eu.getUser().getId()).toList());
        usersToBeNotified.add(userRepository.findById(expense.getInitiator().getId()).get());
        notificationService.saveNotificationGroupExpense(signedInUser, expense, group, usersToBeNotified, NotificationType.GroupExpenseEdited);

        return expense;
    }

    public Expense savePersonalExpense(Expense expense, String category) {
        User signedInUser = signedInUsernameGetter.getUser();
        Group group = groupService.getPersonalGroupByUserId(signedInUser.getId());

        expense.setInitiator(signedInUser);
        expense.setAddedBy(signedInUser);
        expense.setGroup(group);
        if (expense.getDate() == null) {
            expense.setDate(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("UTC")));
        }
        expense.setCategory(categoryService.getOrCreateGroupCategory(category, group));
        expenseRepository.save(expense);

        notificationService.saveNotificationPersonalExpense(signedInUser, expense, NotificationType.PersonalExpenseAdded);

        return expense;
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
        if (expenseRequest.getDate() != null) {
            expense.setDate(expenseRequest.getDate());
        }
        expenseRepository.save(expense);

        notificationService.saveNotificationPersonalExpense(signedInUser, expense, NotificationType.PersonalExpenseEdited);
        return expense;
    }

    private Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_101));
    }

    public void deleteGroupExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        groupService.validateGroupIsNotPersonal(expense.getGroup());

        // update users balances in group
        List<ExpenseUsers> expenseUsers = expenseUsersRepository.findAllByExpenseId(expense.getId());

        HashMap<Long, BigDecimal> userChanges = getInverseBalanceDiff(expenseUsers, expense.getInitiator().getId(), expense.getAmount());
        updateUsersBalanceInGroup(userChanges, expense.getGroup().getId());

        expenseRepository.deleteById(expenseId);

        User signedInUser = signedInUsernameGetter.getUser();
        List<User> usersToBeNotified = userRepository.findAllById(expenseUsers.stream().map(eu -> eu.getUser().getId()).toList());
        notificationService.saveNotificationGroupExpense(signedInUser, expense, expense.getGroup(), usersToBeNotified, NotificationType.GroupExpenseDeleted);
    }

    public void deletePersonalExpenseById(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        User signedInUser = signedInUsernameGetter.getUser();
        groupService.validateGroupIsPersonal(expense.getGroup().getId(), signedInUser.getId());

        expenseRepository.deleteById(expenseId);
        notificationService.saveNotificationPersonalExpense(signedInUser, expense, NotificationType.PersonalExpenseDeleted);
    }

    public void throwExceptionIfExpensesWithGlobalIdNotFound(Long globalId) {
        if (expenseRepository.findById(globalId).isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_101);
        }
    }

    public Page<Expense> getExpensesOfGroup(Long groupId, Integer page, Integer size) {
        groupService.validateGroupExistsAndNotPersonal(groupId);
        User signedInUser = signedInUsernameGetter.getUser();
        groupService.validateUsersInGroup(Set.of(signedInUser.getId()), groupId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return expenseRepository.findAllByGroupId(groupId, pageable);
    }

    public List<Expense> getExpensesOfGroupByIds(Long groupId, List<Long> expenseIds) {
        groupService.validateGroupExistsAndNotPersonal(groupId);
        User signedInUser = signedInUsernameGetter.getUser();
        groupService.validateUsersInGroup(Set.of(signedInUser.getId()), groupId);
        return expenseRepository.findAllByGroupIdAndIdIn(groupId, expenseIds, Sort.by(Sort.Direction.DESC, "date"));
    }

    public Page<Expense> getExpensesOfUser(Integer page, Integer size) {
        User signedInuser = signedInUsernameGetter.getUser();
        Group personalGroup = groupService.getPersonalGroupByUserId(signedInuser.getId());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return expenseRepository.findAllByGroupId(personalGroup.getId(), pageable);
    }

    public List<Expense> getExpensesOfUserByIds(List<Long> expenseIds) {
        User signedInuser = signedInUsernameGetter.getUser();
        Group personalGroup = groupService.getPersonalGroupByUserId(signedInuser.getId());
        return expenseRepository.findAllByGroupIdAndIdIn(personalGroup.getId(), expenseIds, Sort.by(Sort.Direction.DESC, "date"));
    }

    private HashMap<Long, BigDecimal> getBalanceDiff(List<ExpenseUsers> expenseUsers, Long initiatorId,
                                                     BigDecimal expenseAmount) {
        HashMap<Long, BigDecimal> userChanges = new HashMap<>(expenseUsers.size() + 1);
        expenseUsers.forEach(u -> userChanges.put(u.getUser().getId(), u.getAmount().negate()));
        if (userChanges.containsKey(initiatorId)) {
            userChanges.put(initiatorId, expenseAmount.add(userChanges.get(initiatorId)));
        } else {
            userChanges.put(initiatorId, expenseAmount);
        }
        return userChanges;
    }

    private HashMap<Long, BigDecimal> getInverseBalanceDiff(List<ExpenseUsers> expenseUsers, Long initiatorId,
                                                            BigDecimal expenseAmount) {
        HashMap<Long, BigDecimal> userChanges = new HashMap<>(expenseUsers.size() + 1);
        expenseUsers.forEach(u -> userChanges.put(u.getUser().getId(), u.getAmount()));
        if (userChanges.containsKey(initiatorId)) {
            userChanges.put(initiatorId, expenseAmount.negate().add(userChanges.get(initiatorId)));
        } else {
            userChanges.put(initiatorId, expenseAmount.negate());
        }
        return userChanges;
    }

    private void updateUsersBalanceInGroup(Map<Long, BigDecimal> userChanges, Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupIdAndUserIdIn(groupId, userChanges.keySet());
        userGroups.forEach(ug -> {
            ug.setBalance(ug.getBalance().add(userChanges.get(ug.getUser().getId())));
        });
        userGroupRepository.saveAll(userGroups);
    }

    private void validateUsersInGroup(List<ExpenseUsers> users, User initiator, User signedInUser, Long groupId) {
        Set<Long> userIds = users.stream().map(expenseUser ->
                expenseUser.getUser().getId()).collect(Collectors.toSet());
        userIds.add(initiator.getId());
        userIds.add(signedInUser.getId());
        groupService.validateUsersInGroup(userIds, groupId);
    }

    private void validateUserUniqueness(List<ExpenseUsers> users) {
        HashSet<Long> userIds = new HashSet<>(users.size());
        users.forEach(u -> {
            // if element already present in set, it will return false and fail
            if (!userIds.add(u.getUser().getId())) {
                throw new RebalanceException(RebalanceErrorType.RB_105);
            }
        });
    }
}
