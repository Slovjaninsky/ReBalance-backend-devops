package com.rebalance.controller;

import com.rebalance.dto.response.CategoryResponse;
import com.rebalance.mapper.CategoryMapper;
import com.rebalance.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Category management")
@AllArgsConstructor
@RestController
@RequestMapping(APIVersion.current)
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Operation(summary = "Get personal categories")
    @GetMapping("/personal/categories")
    public ResponseEntity<List<CategoryResponse>> getPersonalCategories() {
        return new ResponseEntity<>(
                categoryService.getPersonalCategories().stream()
                        .map(categoryMapper::categoryToResponse).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @Operation(summary = "Get categories of group")
    @GetMapping("/group/{groupId}/categories")
    public ResponseEntity<List<CategoryResponse>> getGroupCategories(@PathVariable("groupId") Long groupId) {
        return new ResponseEntity<>(
                categoryService.getGroupCategories(groupId).stream()
                        .map(categoryMapper::categoryToResponse).collect(Collectors.toList()),
                HttpStatus.OK);
    }
}
