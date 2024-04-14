package com.rebalance.service;

import com.rebalance.entity.Category;
import com.rebalance.entity.Group;
import com.rebalance.entity.User;
import com.rebalance.repository.CategoryRepository;
import com.rebalance.repository.GroupCategoryRepository;
import com.rebalance.repository.GroupRepository;
import com.rebalance.security.SignedInUsernameGetter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    @SuppressWarnings("unused")
    private GroupRepository groupRepository;
    @Mock
    @SuppressWarnings("unused")
    private GroupCategoryRepository groupCategoryRepository;
    @Mock
    private SignedInUsernameGetter signedInUsernameGetter;
    @Mock
    private GroupService groupService;
    @InjectMocks
    private CategoryService categoryService;


    @Test
    public void should_return_categories_with_personal_groups_when_valid_data_provided() {
        Long userId = 1L;
        Long groupId = 2L;
        User user = new User();
        user.setId(userId);
        Group group = new Group();
        group.setId(groupId);
        group.setPersonal(true);
        List<Category> categories = new ArrayList<>();
        when(signedInUsernameGetter.getUser()).thenReturn(user);
        when(groupService.getPersonalGroupByUserId(userId)).thenReturn(group);
        when(categoryRepository.findAllByGroupsGroupId(groupId, Sort.by(Sort.Direction.DESC, "groups.lastUsed"))).thenReturn(categories);

        List<Category> retrievedCategories = categoryService.getPersonalCategories();

        assertEquals(categories, retrievedCategories);
        verify(categoryRepository).findAllByGroupsGroupId(groupId, Sort.by(Sort.Direction.DESC, "groups.lastUsed"));
    }

    @Test
    public void should_throw_exception_when_null_provided_by_repo() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(signedInUsernameGetter.getUser()).thenReturn(user);
        when(groupService.getPersonalGroupByUserId(userId)).thenReturn(null);

        assertThrows(Exception.class, () -> categoryService.getPersonalCategories());
    }

    @Test
    public void should_return_categories_with_public_groups_when_valid_data_provided() {
        Long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);
        group.setPersonal(false);
        List<Category> categories = new ArrayList<>();
        when(groupService.getNotPersonalGroupById(groupId)).thenReturn(group);
        when(categoryRepository.findAllByGroupsGroupId(groupId, Sort.by(Sort.Direction.DESC, "groups.lastUsed"))).thenReturn(categories);

        List<Category> retrievedCategories = categoryService.getGroupCategories(groupId);

        assertEquals(categories, retrievedCategories);
        verify(groupService).getNotPersonalGroupById(groupId);
        verify(categoryRepository).findAllByGroupsGroupId(groupId, Sort.by(Sort.Direction.DESC, "groups.lastUsed"));
    }

    @Test
    public void should_propagate_exception_when_thrown_during_db_operation() {
        Long groupId = 1L;
        when(groupService.getNotPersonalGroupById(groupId)).thenThrow(new RuntimeException("Group not found"));

        assertThrows(RuntimeException.class, () -> categoryService.getGroupCategories(groupId));
    }


}


