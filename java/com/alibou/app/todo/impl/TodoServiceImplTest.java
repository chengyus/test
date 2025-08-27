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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

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

  private Category testCategory;
  private Todo testTodo;
  private TodoRequest testTodoRequest;
  private TodoUpdateRequest testTodoUpdateRequest;
  private TodoResponse testTodoResponse;

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
      .endTime(LocalTime.of(17,0))
      .done(false)
      .user(testUser)
      .category(this.testCategory)
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
      // When
      final String result = todoService.createTodo(null, userId);
      // Then
      assertNotNull(result);
    }
  }
}
