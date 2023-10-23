package com.rebalance.servises;

import com.rebalance.entities.Group;
import com.rebalance.entities.User;
import com.rebalance.entities.UserGroup;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repositories.GroupRepository;
import com.rebalance.repositories.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupService {
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

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
        return getNotPersonalGroupById(groupId).getUsers().stream().map(UserGroup::getUser).collect(Collectors.toList());
    }

    public Group createGroupAndAddUser(Group groupRequest) {
        User user = userService.getUserById(groupRequest.getCreator().getId());

        Group group = createGroup(groupRequest);

        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        userGroupRepository.save(userGroup);

        return group;
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
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

    public void validateGroupExistsAndNotPersonal(Long groupId) {
        if (!groupRepository.existsByIdAndPersonal(groupId, false)) {
            throw new RebalanceException(RebalanceErrorType.RB_201);
        }
    }

    public void validateUsersInGroup(List<Long> users, Long groupId) {
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
