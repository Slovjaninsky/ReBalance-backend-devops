package com.example.databaseservice.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<ExpenseGroup> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    public void deleteGroupById(Long id) {
        groupRepository.deleteById(id);
    }

    public boolean existsById(Long tutorialId) {
        return groupRepository.existsById(tutorialId);
    }
}
