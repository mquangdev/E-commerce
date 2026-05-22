package e_commerce.auth_service.controller;

import e_commerce.auth_service.dto.response.PageResponse;
import e_commerce.auth_service.dto.response.UserResponse;
import e_commerce.auth_service.service.user.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

  private final UserService userService;

  @QueryMapping
  public UserResponse getUserById(@Argument UUID id) {
    return userService.getUserResponseById(id);
  }

  @QueryMapping
  public PageResponse<UserResponse> getAllUsers(@Argument int page, @Argument int size) {
    return userService.getAllUsers(page, size);
  }
}
