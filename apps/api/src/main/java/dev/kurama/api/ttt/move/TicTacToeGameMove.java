package dev.kurama.api.ttt.move;

import dev.kurama.api.core.domain.AbstractEntity;
import dev.kurama.api.ttt.game.TicTacToeGame;
import dev.kurama.api.ttt.player.TicTacToePlayer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor(force = true)
@Entity
public class TicTacToeGameMove extends AbstractEntity implements Serializable {

  @NonNull
  private String cell;

  @NonNull
  private TicTacToePlayer.Token token;

  @NonNull
  private String board;

  @NonNull
  private Integer number;

  @NonNull
  private LocalDateTime movedAt;

  @NonNull
  private Long moveTime;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private TicTacToeGame game;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private TicTacToePlayer player;

}
