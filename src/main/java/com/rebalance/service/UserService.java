package com.rebalance.service;

import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.entity.UserGroup;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
    }

    public List<Group> getAllGroupsOfUser(Long userId) {
        return getUserById(userId).getGroups().stream()
                .map(UserGroup::getGroup)
                .filter(group -> !group.getPersonal()) // do not include personal group
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_002));
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
