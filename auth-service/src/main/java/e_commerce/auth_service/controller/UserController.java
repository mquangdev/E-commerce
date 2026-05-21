package e_commerce.auth_service.controller;

import e_commerce.auth_service.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerce.auth_service.dto.request.CreateUserRequest;
import e_commerce.auth_service.dto.request.UpdateUserRequest;
import e_commerce.auth_service.dto.response.UserResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
    UserResponse response = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateUserRequest request) {
    UserResponse response = userService.updateUser(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
