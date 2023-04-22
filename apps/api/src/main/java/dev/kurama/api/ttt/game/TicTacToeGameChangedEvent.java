package dev.kurama.api.ttt.game;

import dev.kurama.api.core.event.domain.ApplicationEvent;
import dev.kurama.api.ttt.player.TicTacToePlayer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicTacToeGameChangedEvent implements ApplicationEvent {

  private String gameId;
  private TicTacToeGameChangedEventPlayer playerX;
  private TicTacToeGameChangedEventPlayer playerO;
  private TicTacToePlayer.Token turn;
  private TicTacToeGame.Status status;
  private Action action;

  public enum Action {
    CREATED, UPDATED
  }


  @Data
  @Builder
  public static class TicTacToeGameChangedEventPlayer {

    private String id;
    private String username;
  }
}
