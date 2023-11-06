package com.rebalance.service;

import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.GroupRepository;
import com.rebalance.repository.UserRepository;
import com.rebalance.security.SignedInUsernameGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final SignedInUsernameGetter signedInUsernameGetter;

    public List<Group> getMyGroups() {
        User signedInUser = signedInUsernameGetter.getUser();
        return groupRepository.findAllByUsersUserIdAndPersonal(signedInUser.getId(), false);
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
