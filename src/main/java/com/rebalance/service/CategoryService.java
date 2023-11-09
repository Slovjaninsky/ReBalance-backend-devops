package com.rebalance.service;

import com.rebalance.entity.Category;
import com.rebalance.entity.Group;
import com.rebalance.entity.GroupCategory;
import com.rebalance.entity.User;
import com.rebalance.repository.CategoryRepository;
import com.rebalance.repository.GroupCategoryRepository;
import com.rebalance.security.SignedInUsernameGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final SignedInUsernameGetter signedInUsernameGetter;
    private final GroupService groupService;

    public List<Category> getPersonalCategories() {
        User signedInUser = signedInUsernameGetter.getUser();
        Group personalGroup = groupService.getPersonalGroupByUserId(signedInUser.getId());
        return categoryRepository.findAllByGroupsGroupId(personalGroup.getId());
    }

    public List<Category> getGroupCategories(Long groupId) {
        Group group = groupService.getNotPersonalGroupById(groupId);
        return categoryRepository.findAllByGroupsGroupId(group.getId());
    }

    private Category getOrCreateCategory(String name) {
        return categoryRepository.findByNameLike(name)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder().name(name).build()));
    }

    public GroupCategory getOrCreateGroupCategory(String categoryName, Group group) {
        Category category = getOrCreateCategory(categoryName);

        return groupCategoryRepository.findByCategoryAndGroup(category, group)
                .orElseGet(() -> groupCategoryRepository.save(
                        GroupCategory.builder().category(category).group(group).build()));
    }
}
