package dev.kurama.api.core.hateoas.assembler;

import static dev.kurama.api.core.hateoas.relations.RoleRelations.ROLES_REL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.kurama.api.core.hateoas.model.RoleModel;
import dev.kurama.api.core.rest.RoleController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleModelAssembler extends DomainModelAssembler<RoleModel> {

  @Autowired
  private PagedResourcesAssembler<RoleModel> pagedResourcesAssembler;

  @Override
  protected Class<RoleController> getClazz() {
    return RoleController.class;
  }

  @Override
  public WebMvcLinkBuilder getSelfLink(String id) {
    return null;
  }

  @Override
  public WebMvcLinkBuilder getAllLink() {
    return linkTo(methodOn(getClazz()).getAll(null));
  }

  public @NonNull
  PagedModel<RoleModel> toPagedModel(Page<RoleModel> entities) {
    return (PagedModel<RoleModel>) pagedResourcesAssembler.toModel(entities, this)
      .add(getCollectionModelSelfLinkWithRel(getAllLink(), ROLES_REL))
      ;
  }
}
