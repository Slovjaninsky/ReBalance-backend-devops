package com.rebalance.controllers;

import com.rebalance.servises.ExpenseService;
import com.rebalance.servises.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class ImageController {
    private final ImageService imageService;
    private final ExpenseService expenseService;

    @Autowired
    public ImageController(ImageService imageService, ExpenseService expenseService) {
        this.imageService = imageService;
        this.expenseService = expenseService;
    }

    @GetMapping("/expenses/{globalId}/image")
    public ResponseEntity<Map<String, String>> getImageByGlobalId(@PathVariable("globalId") long globalId) {
        String base64Image = imageService.getImageByGlobalId(globalId);
        Map<String, String> response = new HashMap<>();
        response.put("image", base64Image);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/expenses/{globalId}/icon")
    public ResponseEntity<Map<String, String>> getImageIconByGlobalId(@PathVariable("globalId") long globalId) {
        String base64Image = imageService.getImageIconByGlobalId(globalId);
        Map<String, String> response = new HashMap<>();
        response.put("image", base64Image);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/expenses/{globalId}/image")
    public ResponseEntity<HttpStatus> addImageToExpense(@PathVariable("globalId") long globalId, @RequestBody Map<String, String> requestBody) {
        expenseService.throwExceptionIfExpensesWithGlobalIdNotFound(globalId);
        String base64Image = requestBody.get("image");
        imageService.saveImage(base64Image, globalId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
