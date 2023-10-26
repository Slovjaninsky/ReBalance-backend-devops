package com.rebalance.controller;

import com.rebalance.service.ExpenseService;
import com.rebalance.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ExpenseService expenseService;

    @GetMapping("/{expenseId}/image")
    public ResponseEntity<Map<String, String>> getImage(@PathVariable("expenseId") Long expenseId) {
        String base64Image = imageService.getImageByGlobalId(expenseId);

        Map<String, String> response = new HashMap<>();
        response.put("image", base64Image);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{expenseId}/preview")
    public ResponseEntity<Map<String, String>> getImagePreview(@PathVariable("expenseId") Long expenseId) {
        String base64Image = imageService.getImageIconByGlobalId(expenseId);

        Map<String, String> response = new HashMap<>();
        response.put("image", base64Image);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{expenseId}/image")
    public ResponseEntity<String> addImageToExpense(@PathVariable("expenseId") Long expenseId, @RequestBody Map<String, String> requestBody) {
        expenseService.throwExceptionIfExpensesWithGlobalIdNotFound(expenseId);

        String base64Image = requestBody.get("image");
        imageService.saveImage(base64Image, expenseId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
