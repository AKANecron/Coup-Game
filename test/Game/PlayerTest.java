  package Game;

  import Exceptions.InsufficientCoinsException;
  import org.junit.jupiter.api.AfterEach;
  import org.junit.jupiter.api.BeforeEach;
  import org.junit.jupiter.api.Test;

  import static org.junit.jupiter.api.Assertions.*;
  class PlayerTest {

      @BeforeEach
      void setUp() {
          Game.autoStart(1);
      }

      @AfterEach
      void tearDown() {
          //Reset and remove any active players
          Game.resetActivePlayers();
          Game.PLAYERS.clear();
          Game.ALL_PLAYERS.clear();
          Game.deck.resetDeck();
      }

      @Test
      void spendCoinsGood() {
          Player testPlayer = Game.PLAYERS.get(0);
          testPlayer.setCoins(5); //ensure we have 5 coins
          try {
              testPlayer.spendCoins(3); //spend three of those coins
              int expectedCoins = 2;
              assertEquals(expectedCoins, testPlayer.getCoins());
          }
          catch (InsufficientCoinsException e) {e.printStackTrace();}
      }

      @Test
      void spendCoinsBad() {
          //'Bad' case where we spend more coins than the player has
          //This is a bit of a unique case in that we must assert that the correct exception is thrown using assertThrows
          Player testPlayer = Game.PLAYERS.get(0);
          testPlayer.setCoins(4);
          String expectedMessage = "Insufficient coins to perform this effect.";
          Exception e = assertThrows(InsufficientCoinsException.class, () -> testPlayer.spendCoins(5));
          String actualMessage = e.getMessage();
          assertEquals(expectedMessage, actualMessage);
      }

      @Test
      void spendCoinsBoundary() {
          //Boundary case where we spend all the player's coins
          Player testPlayer = Game.PLAYERS.get(0);
          testPlayer.setCoins(5); //ensure we have 5 coins
          try {
              testPlayer.spendCoins(5); //spend all 5 coins
              int expectedCoins = 0;
              assertEquals(expectedCoins, testPlayer.getCoins());
          }
          catch (InsufficientCoinsException e) {e.printStackTrace();}
      }

      @Test
      void loseInfluenceGood() {
          //'Good' test case when user has two influence and loses 1
          Player testPlayer = Game.PLAYERS.get(0);
          testPlayer.loseInfluence(0, new Deck());
          assertEquals(testPlayer.getInfluence(), 1);
      }

      @Test
      void loseInfluenceBad() {
          //'Bad' test case when the user has already lost and their influence is 0, and they lose one influence.
          Player testPlayer = Game.PLAYERS.get(0);
          testPlayer.setInfluence(0);
          testPlayer.setAlive(false);
          testPlayer.loseInfluence(0, new Deck());
          int expectedInfluence = 0;
          boolean expectedAliveStatus = false;
          assertEquals(expectedInfluence, testPlayer.getInfluence());
          assertEquals(expectedAliveStatus, testPlayer.isAlive());
      }

      @Test
      void loseInfluenceBoundary() {
          //'Boundary' test case where the user has 1 influence and loses 2 at once (e.g. if they are assassinated
          //and attempt a challenge against it while already at one influence)
          Player testPlayer = Game.PLAYERS.get(0);
          testPlayer.setInfluence(1); //player starts at 1 influence for this test
          Deck deck = Game.deck;
          testPlayer.loseInfluence(0, deck);
          testPlayer.loseInfluence(0, deck);
          int expectedInfluence = 0;
          boolean expectedAliveStatus = false;
          assertEquals(expectedInfluence, testPlayer.getInfluence());
          assertEquals(expectedAliveStatus, testPlayer.isAlive());
      }
  }