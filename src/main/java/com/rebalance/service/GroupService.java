package com.rebalance.service;

import com.rebalance.dto.response.DebtSettlementResponse;
import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.entity.UserGroup;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.GroupRepository;
import com.rebalance.repository.UserGroupRepository;
import com.rebalance.security.SignedInUsernameGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupService {
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final SignedInUsernameGetter signedInUsernameGetter;

    public Group getGroupInfoById(Long groupId) {
        Group group = getNotPersonalGroupById(groupId);
        User signedInUser = signedInUsernameGetter.getUser();
        // leave only current user to properly map favorite field
        group.getUsers().removeIf(userGroup -> !userGroup.getUser().equals(signedInUser));

        return group;
    }

    public Group getNotPersonalGroupById(Long id) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_201));
        validateGroupIsNotPersonal(group);
        return group;
    }

    public Group getPersonalGroupByUserId(Long userId) {
        return groupRepository.findByCreatorIdAndPersonal(userId, true)
                .orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_201));
    }

    public List<User> getAllUsersOfGroup(Long groupId) {
        return getNotPersonalGroupById(groupId).getUsers().stream().map(ug -> {
            User user = ug.getUser();
            user.setGroups(Set.of(ug));
            return user;
        }).collect(Collectors.toList());
    }

    public Group createGroupAndAddUser(Group groupRequest, User user) {
        groupRequest.setCreator(user);
        Group group = groupRepository.save(groupRequest);

        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        userGroupRepository.save(userGroup);

        return group;
    }

    public Group createGroupAndAddUser(Group groupRequest) {
        User signedInUser = signedInUsernameGetter.getUser();

        Group group = createGroupAndAddUser(groupRequest, signedInUser);
        // leave only current user to properly map favorite field
        group.getUsers().removeIf(userGroup -> !userGroup.getUser().equals(signedInUser));

        return group;
    }

    public UserGroup addUserToGroup(Long groupId, String email) {
        Group group = getNotPersonalGroupById(groupId);
        User user = userService.getUserByEmail(email);

        validateUserNotInGroup(user.getId(), group.getId());

        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        return userGroupRepository.save(userGroup);
    }

    public void setFavorite(Long groupId, Boolean favorite) {
        validateGroupExistsAndNotPersonal(groupId);
        User user = signedInUsernameGetter.getUser();
        UserGroup userGroup = getUserGroup(user.getId(), groupId);

        userGroup.setFavorite(favorite);
        userGroupRepository.save(userGroup);
    }

    private UserGroup getUserGroup(Long userId, Long groupId) {
        return userGroupRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_202));
    }

    public void validateGroupExists(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new RebalanceException(RebalanceErrorType.RB_201);
        }
    }

    public List<DebtSettlementResponse> getDebtSettlement(Long groupId) {
        User signedInUser = signedInUsernameGetter.getUser();
        validateUsersInGroup(Set.of(signedInUser.getId()), groupId);

        List<UserGroup> userGroups = userGroupRepository.findAllByGroupId(groupId);

        // calculate sum of balances
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (UserGroup ug : userGroups) {
            totalBalance = totalBalance.add(ug.getBalance().abs());
        }

        // greedy get settlements
        List<DebtSettlementResponse> settlements = new ArrayList<>();
        while (true) {
            UserGroup maxPositive = null;
            UserGroup maxNegative = null;

            for (UserGroup ug : userGroups) {
                if (ug.getBalance().compareTo(BigDecimal.ZERO) > 0 &&
                        (maxPositive == null || ug.getBalance().compareTo(maxPositive.getBalance()) > 0)) {
                    maxPositive = ug;
                }
                if (ug.getBalance().compareTo(BigDecimal.ZERO) < 0 &&
                        (maxNegative == null || ug.getBalance().compareTo(maxNegative.getBalance()) < 0)) {
                    maxNegative = ug;
                }
            }

            if (maxPositive == null || maxNegative == null) {
                break;
            }

            BigDecimal amount = maxPositive.getBalance().min(maxNegative.getBalance().negate());
            maxPositive.setBalance(maxPositive.getBalance().subtract(amount));
            maxNegative.setBalance(maxNegative.getBalance().add(amount));
            settlements.add(new DebtSettlementResponse(
                    maxNegative.getUser().getId(), maxNegative.getUser().getNickname(),
                    maxPositive.getUser().getId(), maxPositive.getUser().getNickname(),
                    amount.doubleValue()));
        }

        return settlements;
    }

    public void validateGroupExistsAndNotPersonal(Long groupId) {
        if (!groupRepository.existsByIdAndPersonal(groupId, false)) {
            throw new RebalanceException(RebalanceErrorType.RB_201);
        }
    }

    public void validateUsersInGroup(Set<Long> users, Long groupId) {
        if (userGroupRepository.countByGroupIdAndUserIdIn(groupId, users) != users.size()) {
            throw new RebalanceException(RebalanceErrorType.RB_202);
        }
    }

    public void validateGroupIsPersonal(Long groupId, Long userId) {
        if (!groupRepository.existsByIdAndCreatorIdAndPersonal(groupId, userId, true)) {
            throw new RebalanceException(RebalanceErrorType.RB_204);
        }
    }

    public void validateGroupIsNotPersonal(Group group) {
        if (group.getPersonal()) {
            throw new RebalanceException(RebalanceErrorType.RB_205);
        }
    }

    private void validateUserNotInGroup(Long userId, Long groupId) {
        if (userGroupRepository.countByGroupIdAndUserId(groupId, userId) != 0) {
            throw new RebalanceException(RebalanceErrorType.RB_203);
        }
    }
}
