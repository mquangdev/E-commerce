package e_commerce.auth_service.controller;

import e_commerce.auth_service.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerce.auth_service.dto.request.CreateRoleRequest;
import e_commerce.auth_service.dto.request.UpdateRoleRequest;
import e_commerce.auth_service.dto.response.RoleResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  @PostMapping
  public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
    RoleResponse response = roleService.createRole(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RoleResponse> updateRole(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateRoleRequest request) {
    RoleResponse response = roleService.updateRole(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
    roleService.deleteRole(id);
    return ResponseEntity.noContent().build();
  }
}
