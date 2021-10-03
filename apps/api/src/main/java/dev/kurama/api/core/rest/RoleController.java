package dev.kurama.api.core.rest;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import dev.kurama.api.core.exception.domain.ImmutableRoleException;
import dev.kurama.api.core.exception.domain.exists.RoleExistsException;
import dev.kurama.api.core.exception.domain.not.found.RoleNotFoundException;
import dev.kurama.api.core.facade.RoleFacade;
import dev.kurama.api.core.hateoas.input.RoleInput;
import dev.kurama.api.core.hateoas.model.RoleModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class RoleController {

  @NonNull
  private final RoleFacade roleFacade;

  @GetMapping()
  @PreAuthorize("hasAuthority('role:read')")
  public ResponseEntity<PagedModel<RoleModel>> getAll(
    @PageableDefault(page = 0, size = DEFAULT_PAGE_SIZE) Pageable pageable) {
    return ok().body(roleFacade.getAll(pageable));
  }

  @GetMapping("/{roleId}")
  @PreAuthorize("hasAuthority('role:read')")
  public ResponseEntity<RoleModel> get(@PathVariable("roleId") String roleId) {
    return ok().body(roleFacade.findByRoleId(roleId));
  }

  @PostMapping()
  @PreAuthorize("hasAuthority('role:create')")
  public ResponseEntity<RoleModel> create(@RequestBody RoleInput roleInput) throws RoleExistsException {
    return ok().body(roleFacade.create(roleInput));
  }

  @PatchMapping("/{roleId}")
  @PreAuthorize("hasAuthority('role:update')")
  public ResponseEntity<RoleModel> update(@PathVariable("roleId") String roleId, @RequestBody RoleInput roleInput)
    throws RoleNotFoundException, ImmutableRoleException {
    return ok().body(roleFacade.update(roleId, roleInput));
  }

  @DeleteMapping("/{roleId}")
  @PreAuthorize("hasAuthority('role:delete')")
  public ResponseEntity<Void> delete(@PathVariable("roleId") String roleId)
    throws ImmutableRoleException, RoleNotFoundException {
    roleFacade.delete(roleId);
    return noContent().build();
  }

}
