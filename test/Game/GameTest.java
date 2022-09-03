  package Game;

  import org.junit.jupiter.api.AfterEach;
  import org.junit.jupiter.api.BeforeEach;
  import org.junit.jupiter.api.Test;

  import static org.junit.jupiter.api.Assertions.*;

  class GameTest {
      @BeforeEach
      void setUp() {
          Game.autoStart(6);
      }

      @AfterEach
      void tearDown() {
          Game.resetActivePlayers();
          Game.PLAYERS.clear();
          Game.ALL_PLAYERS.clear();
          Game.deck.resetDeck();
      }

      @Test
      void findTurnPlayerGood() {
          //Good case where the next player in the array is alive.
          Player activePlayer = Game.PLAYERS.get(0);
          Player nextPlayer = Game.findTurnPlayer(activePlayer);
          Player expectedPlayer = Game.PLAYERS.get(1);
          assertEquals(expectedPlayer, nextPlayer);
      }

      @Test
      void findTurnPlayerBad() {
          //'Bad' Case where the next two players are not alive
          Player activePlayer = Game.PLAYERS.get(0);
          Game.PLAYERS.get(1).setAlive(false); //we will kill the next player
          Game.PLAYERS.get(2).setAlive(false); //and the next one too
          Player nextPlayer = Game.findTurnPlayer(activePlayer);
          Player expectedPlayer = Game.PLAYERS.get(3);
          assertEquals(expectedPlayer, nextPlayer);
      }

      @Test
      void findTurnPlayerBoundary() {
          //Boundary case where we must skip from the last player back to the first
          Player activePlayer = Game.PLAYERS.get(5);
          Player nextPlayer = Game.findTurnPlayer(activePlayer);
          Player expectedPlayer = Game.PLAYERS.get(0);
          assertEquals(expectedPlayer, nextPlayer);
      }
  }