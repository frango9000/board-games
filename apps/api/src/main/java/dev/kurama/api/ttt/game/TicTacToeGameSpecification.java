package dev.kurama.api.ttt.game;

import static dev.kurama.api.core.utility.AuthorityUtils.getCurrentUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import dev.kurama.api.ttt.game.input.TicTacToeGameFilterInput;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public record TicTacToeGameSpecification(TicTacToeGameFilterInput filter) implements Specification<TicTacToeGame> {

  @Nullable
  @Override
  public Predicate toPredicate(@NonNull Root<TicTacToeGame> root,
                               @NonNull CriteriaQuery<?> query,
                               @NonNull CriteriaBuilder criteriaBuilder) {
    ArrayList<Predicate> predicates = new ArrayList<>();

    if (isTrue(filter.getMyGames())) {
      predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("playerX").get("id"), getCurrentUserId()),
        criteriaBuilder.equal(root.get("playerO").get("id"), getCurrentUserId())));
    }

    if (filter.getPlayer() != null && !filter.getPlayer().isEmpty()) {
      predicates.add(criteriaBuilder.or(
        criteriaBuilder.like(criteriaBuilder.lower(root.get("playerX").get("user").get("username")),
          "%" + filter.getPlayer().toLowerCase() + "%"),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("playerO").get("user").get("username")),
          "%" + filter.getPlayer().toLowerCase() + "%")));
    }

    if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
      predicates.add(criteriaBuilder.and(
        root.get("status").in(filter.getStatus().stream().map(TicTacToeGame.Status::valueOf).toList())));
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  public record MyGamesOrPublic() implements Specification<TicTacToeGame> {

    @Nullable
    @Override
    public Predicate toPredicate(@NonNull Root<TicTacToeGame> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.or(criteriaBuilder.equal(root.get("playerX").get("id"), getCurrentUserId()),
        criteriaBuilder.equal(root.get("playerO").get("id"), getCurrentUserId()),
        criteriaBuilder.equal(root.get("isPrivate"), false));
    }
  }

}
