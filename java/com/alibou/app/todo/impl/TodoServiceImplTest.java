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
import static org.mockito.Mockito.*;

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

    this.todoUpdateRequest = TodoUpdateRequest.builder()
        .title("Updated Todo")
        .description(("Updated Description"))
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(2))
        .startTime(LocalTime.of(11, 0))
        .endTime(LocalTime.of(19, 0))
        .categoryId("category-123")
        .build();

    this.todoResponse = TodoResponse.builder()
        .id("todo-1234")
        .title("Test todo")
        .description("Test Description")
        .startDate(LocalDate.now())
        .endDate(LocalDate.now().plusDays(1))
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(17, 0))
        .done(false)
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

      assertEquals("No category was found for that user with id " + todoRequest.getCategoryId(),
          exception.getMessage());
      verify(categoryRepository, times(1)).findByIdAndUserId(todoRequest.getCategoryId(), userId);
      verifyNoInteractions(todoMapper);
      verifyNoInteractions(todoRepository);
    }

    @Test
    @DisplayName("Should Handle null catgegoryId in request")
    void shouldHandleNullCatIdInRequest() {
      // Given
      final String userId = "user-123";
      todoRequest.setCategoryId(null);

      when(categoryRepository.findByIdAndUserId(null, userId))
          .thenReturn(Optional.empty());

      // When & Then
      final EntityNotFoundException exception = assertThrows(
          EntityNotFoundException.class,
          () -> TodoServiceImplTest.this.todoService.createTodo(TodoServiceImplTest.this.todoRequest, userId));

      assertNotNull(exception);
      assertEquals("No category was found for that user with id " + todoRequest.getCategoryId(),
          exception.getMessage());
      verify(categoryRepository, times(1)).findByIdAndUserId(todoRequest.getCategoryId(), userId);
      verifyNoInteractions(todoMapper);
      verifyNoInteractions(todoRepository);
    }
  }

  @Nested
  @DisplayName("Update Todo Tests")
  class UpdateTodoTests {
    @Test
    @DisplayName("Should update successfully a Todo when todo and category exist")
    void shouldSuccessfullyUpdateTodo() {
      // Given
      final String userId = "user-123";
      final String todoId = "todo-123";

      when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
      when(categoryRepository.findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId))
          .thenReturn(Optional.of(testCategory));
      when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

      // When
      todoService.updateTodo(todoUpdateRequest, todoId, userId);

      // Then
      verify(todoRepository, times(1)).findById(todoId);
      verify(categoryRepository, times(1)).findByIdAndUserId(testTodo.getCategory().getId(), userId);
      verify(todoMapper).mergerTodo(testTodo, todoUpdateRequest);
      verify(todoRepository).save(testTodo);
      // Verify category is set
      assertEquals(testCategory, testTodo.getCategory());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when Todo not found")
    void shouldThrowEntityNotFoundExceptionWhenTodoNotFound() {
      final String userId = "user-123";
      final String todoId = "todo-123";
      when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

      final EntityNotFoundException exception = assertThrows(
          EntityNotFoundException.class,
          () -> todoService.updateTodo(todoUpdateRequest, todoId, userId));

      assertEquals("Todo not found with id: " + todoId, exception.getMessage());
      verify(todoRepository, times(1)).findById(todoId);
      verifyNoInteractions(categoryRepository);
      verifyNoInteractions(todoMapper);
      verify(todoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when Category not found")
    void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
      final String userId = "user-123";
      final String todoId = "todo-123";
      when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
      when(categoryRepository.findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId))
          .thenReturn(Optional.empty());

      final EntityNotFoundException exception = assertThrows(
          EntityNotFoundException.class,
          () -> todoService.updateTodo(todoUpdateRequest, todoId, userId));

      assertEquals("No category was found for that user with id " + todoUpdateRequest.getCategoryId(),
          exception.getMessage());
      verify(todoRepository, times(1)).findById(todoId);
      verify(categoryRepository).findByIdAndUserId(todoUpdateRequest.getCategoryId(), userId);
      verifyNoInteractions(todoMapper);
      verify(todoRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Find Todo By Id Tests")
  class FindTodoByIdTests {
    @Test
    @DisplayName("Should return todo requests when todo exists")
    void shouldReturnTodoResponse() {
      // Given
      final String todoId = "todo-123";
      when(todoRepository.findById(todoId)).thenReturn(Optional.of(testTodo));
      when(todoMapper.toTodoResponse(testTodo)).thenReturn(todoResponse);

      // When
      final TodoResponse result = todoService.findTodoById(todoId);

      // Then
      assertNotNull(result);
      assertEquals(todoResponse, result);
      verify(todoRepository).findById(todoId);
      verify(todoMapper).toTodoResponse(testTodo);

    }
  }

}
