package dev.kurama.api.core.hateoas.processor;

import dev.kurama.api.core.hateoas.model.GlobalSettingsModel;
import dev.kurama.api.core.rest.GlobalSettingsController;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static dev.kurama.api.core.authority.GlobalSettingsAuthority.GLOBAL_SETTINGS_UPDATE;
import static dev.kurama.api.core.hateoas.relations.HateoasRelations.SELF;
import static dev.kurama.api.core.utility.AuthorityUtils.hasAuthority;
import static dev.kurama.api.core.utility.HateoasUtils.withDefaultAffordance;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class GlobalSettingsModelProcessor implements RepresentationModelProcessor<GlobalSettingsModel> {


  @Override
  public @NonNull GlobalSettingsModel process(@NonNull GlobalSettingsModel globalSettingsModel) {
    return globalSettingsModel
      .add(getSelfLink())
      .mapLinkIf(hasAuthority(GLOBAL_SETTINGS_UPDATE),
        LinkRelation.of(SELF),
        link -> link.andAffordance(getUpdateAffordance()))
      ;
  }

  @SneakyThrows
  public @NonNull Link getSelfLink() {
    return withDefaultAffordance(linkTo(methodOn(GlobalSettingsController.class).get()).withSelfRel());
  }

  @SneakyThrows
  private @NonNull Affordance getUpdateAffordance() {
    return afford(methodOn(GlobalSettingsController.class).update(null));
  }
}
