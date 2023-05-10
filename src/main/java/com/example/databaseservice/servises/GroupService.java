package com.example.databaseservice.servises;

import com.example.databaseservice.entities.ExpenseGroup;
import com.example.databaseservice.exceptions.GroupNotFoundException;
import com.example.databaseservice.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<ExpenseGroup> findAllGroups() {
        return groupRepository.findAll();
    }

    public ExpenseGroup saveGroup(ExpenseGroup expenseGroup) {
        return groupRepository.save(expenseGroup);
    }

    public ExpenseGroup getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException("Not found Group with id = " + id));
    }

    public ExpenseGroup updateGroup(Long id, @RequestBody ExpenseGroup inputGroup) {
        ExpenseGroup group = getGroupById(id);
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
}
