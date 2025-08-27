package com.alibou.app.todo.impl;

import com.alibou.app.category.CategoryRepository;
import com.alibou.app.todo.TodoMapper;
import com.alibou.app.todo.TodoRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl Unit Test")
class TodoServiceImplTest {

  @Mock
  private TodoRepository todoRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private TodoMapper todoMapper;

  @InjectMocks
  private TodoServiceImpl todoService;

  @Nested
  @DisplayName("Create Todo Tests")
  class CreateTodoTests {

    @Test
    @DisplayName("Should create todo successfully when valid request and cstegory exists.")
    void ShouldCreateTodoSuccessfully() {
      // Given
      final String userId = "user-123";
      // When
      final String result = todoService.createTodo(null, userId);
      // Then
      assertNotNull(result);
    }
  }
}
