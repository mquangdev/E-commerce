package e_commerce.auth_service.controller;

import e_commerce.auth_service.dto.response.PageResponse;
import e_commerce.auth_service.dto.response.RoleResponse;
import e_commerce.auth_service.service.role.RoleService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoleGraphQLController {

  private final RoleService roleService;

  @QueryMapping
  public RoleResponse getRoleById(@Argument UUID id) {
    return roleService.getRoleResponseById(id);
  }

  @QueryMapping
  public PageResponse<RoleResponse> getAllRoles(@Argument int page, @Argument int size) {
    return roleService.getAllRoles(page, size);
  }
}
