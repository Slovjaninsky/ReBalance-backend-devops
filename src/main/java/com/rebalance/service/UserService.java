package com.rebalance.service;

import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.entity.UserGroup;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.GroupRepository;
import com.rebalance.repository.UserGroupRepository;
import com.rebalance.repository.UserRepository;
import com.rebalance.security.SignedInUsernameGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final SignedInUsernameGetter signedInUsernameGetter;

    public List<Group> getMyGroups() {
        User signedInUser = signedInUsernameGetter.getUser();
        // get groups of user
        List<Group> groups = groupRepository.findAllByUsersUserIdAndPersonal(signedInUser.getId(), false);
        // get UserGroup for found groups and with current user id
        List<UserGroup> users = userGroupRepository.findByUserIdAndGroupIdIn(signedInUser.getId(), groups.stream()
                .map(Group::getId).toList());
        // set users (only current user) for each group
        groups.forEach(group -> group.setUsers(users.stream()
                .filter(userGroup -> userGroup.getGroup().equals(group))
                .collect(Collectors.toSet())));
        return groups;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
    }

    public User getLoggedInUser() {
        User user = signedInUsernameGetter.getUser();
        user.setCreatedGroups(
                new HashSet<>(List.of(
                        groupRepository.findByCreatorIdAndPersonal(user.getId(), true).get())
                ));
        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void validateUserNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RebalanceException(RebalanceErrorType.RB_001);
        }
    }
}
