package com.alibou.app.todo.impl;

import com.alibou.app.category.Category;
import com.alibou.app.category.CategoryRepository;
import com.alibou.app.todo.Todo;
import com.alibou.app.todo.TodoMapper;
import com.alibou.app.todo.TodoRepository;
import com.alibou.app.todo.request.TodoRequest;
import com.alibou.app.todo.request.TodoUpdateRequest;
import com.alibou.app.todo.response.TodoResponse;
import com.alibou.app.user.User;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

  private Category testCategory;
  private Todo testTodo;
  private TodoRequest todoRequest;
  private TodoUpdateRequest todoUpdateRequest;
  private TodoResponse todoResponse;

  @BeforeEach
  void setUp() {
    final User testUser = User.builder()
        .id("user-123")
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .build();

    this.testCategory = Category.builder()
        .id("category-123")
        .name("Work")
        .description("Work related todos")
        .build();

    this.testTodo = Todo.builder()
        .id("todo-123")
        .title("Test todo")
        .description("test Description")
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(1))
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(17, 0))
        .done(false)
        .user(testUser)
        .category(this.testCategory)
        .build();

    this.todoRequest = TodoRequest.builder()
        .title("New Todo")
        .description(("New Description"))
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(1))
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(17, 0))
        .categoryId("category-123")
        .build();

  }

  @Nested
  @DisplayName("Create Todo Tests")
  class CreateTodoTests {

    @Test
    @DisplayName("Should create todo successfully when valid request and cstegory exists.")
    void ShouldCreateTodoSuccessfully() {
      // Given
      final String userId = "user-123";
      when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
          .thenReturn(Optional.of(testCategory));
      when(todoMapper.toTodo(todoRequest)).thenReturn(testTodo);
      when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);
      // When
      final String result = todoService.createTodo(todoRequest, userId);
      // Then
      assertNotNull(result);
      assertEquals("todo-123", result);
      verify(categoryRepository, times(1)).findByIdAndUserId(todoRequest.getCategoryId(), userId);
      verify(todoMapper, times(1)).toTodo(todoRequest);
      verify(todoRepository, times(1)).save(testTodo);

      // Verify category is set on todo.
      verify(todoRepository)
          .save(argThat(todo -> todo.getCategory() != null && todo.getCategory().getId().equals("category-123")));

    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when category is not found.")
    void shoudlThrowEntityNotFoundExceptionWhenCategoryNotFound() {
      // Given
      final String userId = "user-123";
      when(categoryRepository.findByIdAndUserId(todoRequest.getCategoryId(), userId))
          .thenReturn(Optional.empty());

      // When & Then
      final EntityNotFoundException exception = assertThrows(
          EntityNotFoundException.class,
          () -> TodoServiceImplTest.this.todoService.createTodo(TodoServiceImplTest.this.todoRequest, userId));

      assertEquals("No category was found for that user with id" + todoRequest.getCategoryId(),
          exception.getMessage());
      verify(categoryRepository, times(1)).findByIdAndUserId(todoRequest.getCategoryId(), userId);
      verify(todoMapper, times(0)).toTodo(todoRequest);
    }
  }
}
