package com.alibou.app.category;

import com.alibou.app.user.User;
import com.alibou.app.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CategoryIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCategoryOwnership() {
        // Given
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase("ali@mail.com");
        assertTrue(userOptional.isPresent(), "Test user should be seeded");
        User user = userOptional.get();

        // When
        var categories = categoryRepository.findAll();

        // Then
        assertFalse(categories.isEmpty(), "There should be at least one seeded category");
        Category category = categories.get(0);
        assertNotNull(category.getCreatedBy(), "Category should have a creator");
        assertEquals(user.getId(), category.getCreatedBy(), "The first category should be created by the test user");
    }
}
