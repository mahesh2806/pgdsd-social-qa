package com.upgrad.quora.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upgrad.models.User;
import org.upgrad.services.CategoryService;
import org.upgrad.services.UserService;

import javax.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @DeleteMapping("/api/admin/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "userId") int userId, HttpSession httpSession) {
        if (httpSession.getAttribute("currUser") == null) {
            return new ResponseEntity<>("Please Login first to access this endpoint!", HttpStatus.UNAUTHORIZED);
        } else if (((User) httpSession.getAttribute("currUser")).getRole().equalsIgnoreCase("admin")) {
            userService.deleteUser(userId);
            return new ResponseEntity<>("User with userId " + userId + " deleted successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("You do not have rights to delete a user!", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/api/admin/users/all")
    public ResponseEntity<?> getAllUsers(HttpSession httpSession) {
        if (httpSession.getAttribute("currUser") == null) {
            return new ResponseEntity<>("Please Login first to access this endpoint!", HttpStatus.UNAUTHORIZED);
        } else if (((User) httpSession.getAttribute("currUser")).getRole().equalsIgnoreCase("admin")) {
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("You do not have rights to access all users!", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/api/admin/category")
    public ResponseEntity<?> createCategory(@RequestParam(value = "categoryTitle") String categoryTitle,
                                              @RequestParam(value = "description") String categoryDescription,
                                              HttpSession httpSession) {
        if (httpSession.getAttribute("currUser") == null) {
            return new ResponseEntity<>("Please Login first to access this endpoint!", HttpStatus.UNAUTHORIZED);
        } else if (((User) httpSession.getAttribute("currUser")).getRole().equalsIgnoreCase("admin")) {
            categoryService.addCategory(categoryTitle, categoryDescription);
            return new ResponseEntity<>(categoryTitle + " category added successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("You do not have rights to add categories.", HttpStatus.UNAUTHORIZED);
        }
    }
}