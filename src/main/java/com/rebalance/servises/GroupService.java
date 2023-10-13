package com.rebalance.servises;

import com.rebalance.entities.Group;
import com.rebalance.entities.User;
import com.rebalance.entities.UserGroup;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> findAllGroups() {
        return groupRepository.findAll();
    }

    public List<Group> findAllGroupsByUserId(Long userId) {
        return groupRepository.findAllByUsersId(userId);
    }

    public Set<User> findAllUsersOfGroup(Long groupId) {
        return getGroupById(groupId).getUsers().stream().map(UserGroup::getUser).collect(Collectors.toSet());
    }

    public void saveGroup(Group group) {
        groupRepository.save(group);
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_201));
    }

    public Group updateGroup(Long id, @RequestBody Group inputGroup) {
        Group group = getGroupById(id);
        if (inputGroup.getName() != null) {
            group.setName(inputGroup.getName());
        }
        if (inputGroup.getCurrency() != null) {
            group.setCurrency(inputGroup.getCurrency());
        }
        return groupRepository.save(group);
    }

    public void deleteGroupById(Long id) {
        throwExceptionIfGroupNotFoundById(id);
        groupRepository.deleteById(id);
    }

    public boolean existsById(Long tutorialId) {
        return groupRepository.existsById(tutorialId);
    }

    public void throwExceptionIfGroupNotFoundById(Long id) {
        getGroupById(id);
    }

    public void addUserToGroup(Long groupId, String email) {
        //TODO: implement
    }
}
