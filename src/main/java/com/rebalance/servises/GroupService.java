package com.rebalance.servises;

import com.rebalance.entities.Group;
import com.rebalance.entities.User;
import com.rebalance.entities.UserGroup;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repositories.GroupRepository;
import com.rebalance.repositories.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupService {
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_201));
    }

    public Group getGroupByIdWithExpenses(Long id) {
        Optional<Group> group = groupRepository.findById(id);

        if (group.isPresent()) {
            Hibernate.initialize(group.get().getExpenses());
        } else {
            throw new RebalanceException(RebalanceErrorType.RB_201);
        }

        return group.get();
    }

    public List<User> getAllUsersOfGroup(Long groupId) {
        return getGroupById(groupId).getUsers().stream().map(UserGroup::getUser).collect(Collectors.toList());
    }

    public void createGroup(Group group) {
        groupRepository.save(group);
    }

    public void addUserToGroup(Long groupId, String email) {
        Group group = getGroupById(groupId);
        User user = userService.getUserByEmail(email);

        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);

        userGroupRepository.save(userGroup);
    }

    //TODO: check
    public void validateUsersInGroup(List<Long> users, Long groupId) {
        if (!userGroupRepository.existsByGroupIdAndUserIdIn(groupId, users)) {
            throw new RebalanceException(RebalanceErrorType.RB_202);
        }
    }
}
