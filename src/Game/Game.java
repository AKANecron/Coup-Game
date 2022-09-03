package Game;

import Effects.*;
import java.util.*;

// A static helper class to the App.java application.
// This class effectively handles the implementation of the game logic.
// The Tools class handles generic functions like getting valid inputs and printing messages
// while the App class runs and handles the flow of gameplay.
public abstract class Game {
    public static final Deck deck = Deck.getInstance(); //gets the singleton deck instance
    public static final Effect[] EFFECTS = {
            new Income(),
            new ForeignAid(),
            new Coup(deck),
            new Tax(),
            new Assassinate(deck),
            new Exchange(deck),
            new Steal()
    };
    public static final ArrayList<Player> ALL_PLAYERS = new ArrayList<>();
    public static final ArrayList<Player> PLAYERS = new ArrayList<>();

    public static void showCards(Player player) {
        if (player.getHand().size() > 0) {
            Tools.showOnlyMessage("Showing cards for " + player.getName() + ". Press enter if this is you.", 0); //confirm correct player.
            Tools.input.nextLine();
            System.out.print("Revealing cards in"); // add some lag 
            for(int i = 3; i > 0; i--){
                Tools.showMessage(" " + i,0.25);
                for(int j = 0; j < 3; j++){
                    Tools.showMessage(".", 0.25);
                }
            }
            for(Card card : player.getHand()) {
                System.out.println();
                System.out.println(card);
            }
            Tools.showMessage("Coin Balance: " + player.getCoins() + "\n", 1.1);
            System.out.println("Press enter to continue.");
            Tools.input.nextLine();
            Tools.clearConsole();
        }

        else Tools.showOnlyMessage(player.getName() + " has no cards left.", 0);
    }

    public static int checkEffectBluff(Player player, String effect) {
        if (effect.equals("income") || effect.equals("foreign aid") || effect.equals("coup")) return 2; //if the effect isn't refutable
        else {
            for (int i = 0; i < player.getHand().size(); i++){
                if (player.getHand().get(i).getEffect().equals(effect)) return i; //if player is not bluffing, index of card to be swapped
            }
            return -1; //if they are bluffing
        }
    }

    public static int checkCounteractBluff(Player player, String counteract) {
        for (int i = 0; i < player.getHand().size(); i++){
            if(player.getHand().get(i).getCounteraction().equals(counteract)) return i; //if blocker is not bluffing, 
        }
        return -1; //if blocker is bluffing
    }

    public static boolean registerPlayer(String playerName, int currentIndex){
        if (findAnyPlayer(playerName) == null) {
            ALL_PLAYERS.add(new Player(playerName, currentIndex));
            return true;
        }
        return false;
    }

    public static void registerPlayers(int numPlayers) {
        if (numPlayers >= 3 && numPlayers <= 6) {
            for (int i = 0; i < numPlayers; i++) {
                boolean successful = false;
                while (!successful) {
                    System.out.print("Please enter the name for Player " + (i + 1) + ". ");
                    System.out.print("Note - Player names cannot contain spaces: ");
                    String playerName = Tools.input.next();Tools.input.nextLine();
                    successful = registerPlayer(playerName, i);
                }
                System.out.println("Thanks for joining, " + ALL_PLAYERS.get(ALL_PLAYERS.size()-1).getName() + "!");
                System.out.println("Please press enter to continue.");
                Tools.input.nextLine();
             }
        }
    }

    public static Player[] findWaitingPlayers(Player activePlayer) {
        Player[] waitingPlayers = new Player[PLAYERS.size() - 1];
        int otherPlayersIndex = 0;
        for (Player player : Game.PLAYERS) {
            if (!player.equals(activePlayer)) {
                waitingPlayers[otherPlayersIndex++] = player;
            }
        }
        return waitingPlayers;
    }

    public static void resetActivePlayers() {
        PLAYERS.clear();
        PLAYERS.addAll(ALL_PLAYERS);
        for (Player player : Game.PLAYERS) {
            player.resetPlayer();
        }
    }

    //does not need unit test
    public static String getWinner() {
        /*
         * Checks if only one active player is left at the end of the round.
         * If so, prints a message returns that player's name as a string.
         * Otherwise, returns null.
         */
        if(PLAYERS.size() == 1) {
            String winner = PLAYERS.get(0).getName();
            System.out.println(winner + " is the winner of this round!");
            return winner;
        }
        return null;
    }

    public static Player findTurnPlayer(Player lastPlayer) {
        int turnPlayerNum = (lastPlayer.getNumber() + 1) % ALL_PLAYERS.size();
        Player expectedPlayer;
        expectedPlayer = findPlayerByNum(turnPlayerNum);
        for (int i = 0; i < ALL_PLAYERS.size(); i++){
            assert expectedPlayer != null;
            if (!expectedPlayer.isAlive()) {
                expectedPlayer = ALL_PLAYERS.get(++turnPlayerNum % ALL_PLAYERS.size());
            } 
            else break;
        }
        return expectedPlayer;
    }

    public static Player findLivingPlayer(String name){
        for(Player player : PLAYERS) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        
        return null;
    }

    public static Player findAnyPlayer(String name){
        for(Player player : ALL_PLAYERS) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        
        return null;
    }

    public static Player findPlayerByNum(int num){
        for(Player player : ALL_PLAYERS) {
            if (player.getNumber() == num) return player;
        }

        return null;
    }

    public static Player setTargetPlayer(Player activePlayer, Effect declaredEffect) {
        if (declaredEffect.isTargeted()) {
            Player[] otherPlayers = findWaitingPlayers(activePlayer);
            String[] otherPlayerNames = new String[otherPlayers.length]; //names of non-active players for validation
            for (int i = 0; i < otherPlayerNames.length; i++) {
                otherPlayerNames[i] = otherPlayers[i].getName();
            }
            String targetPrompt = "Enter the name of the target player: ";
            String target = Tools.promptInput(targetPrompt, "Sorry, that isn't valid. " + targetPrompt, otherPlayerNames);
            return Game.findLivingPlayer(target);
        }

        else return activePlayer;
    }

    public static void updatePlayerList() {
        for (int i = 0; i < PLAYERS.size(); i++) {
            if(!PLAYERS.get(i).isAlive()){
                PLAYERS.remove(i);
            }
        }
    }

    public static boolean [] processChallenges(Player activePlayer, boolean apIsBluffing, int cardFlag) {
        boolean[] challengeResult = {false, false}; //first element stores whether a challenge was issued, second one whether it was successful
        for (Player player : findWaitingPlayers(activePlayer)) {
            challengeResult = processChallenge(player, activePlayer, apIsBluffing, cardFlag);
            if (challengeResult[0]) break; //stop asking once a challenge actually is issued.
        }
        return challengeResult;
    }

    public static boolean [] processChallenge(Player challenger, Player challengedPlayer, boolean cpIsBluffing,
                                              int cardFlag) {
        String[] yN = {"y", "n"};
        boolean[] challengeResult = {false, false}; //first element stores whether a challenge was issued, second one whether it was successful
        String willChallenge = Tools.promptInput(challenger.getName() + ", would you like to challenge? [y/n]: ",
                "Sorry, I didn't understand. Would you like to challenge? [y/n]: ", yN).toLowerCase();
        if (willChallenge.equals("y")) {
            challengeResult[0] = true; //at this point, a challenge was issued, so this is true
            challengeResult[1] = challenger.challenge(challengedPlayer, cpIsBluffing, cardFlag, deck); //this stores the result of the challenge.
        }
        return challengeResult;
    }
    public static boolean[] processBlock(Player activePlayer, Player targetPlayer, Effect declaredEffect) {
        String[] yN = {"y", "n"};
        String blockPrompt = targetPlayer.getName() + ", would you like to counteract? [y/n]: ";
        String willBlock = Tools.promptInput(blockPrompt, "Sorry, that isn't valid. " + blockPrompt, yN).toLowerCase();
        boolean blockDeclared = false;
        boolean blockSuccessful = false;
        if (willBlock.equals("y")) {
            blockDeclared = true;
            String counteract = targetPlayer.counteract(declaredEffect);
            int blockFlag = Game.checkCounteractBluff(targetPlayer, counteract);
            boolean tpIsBluffing = blockFlag < 0;
            boolean blockChallenge = Game.processChallenge(activePlayer, targetPlayer, tpIsBluffing, blockFlag)[1];
            blockSuccessful = !blockChallenge; //successful challenge means failed block and vice versa, hence the not operator.
        }
        return new boolean[] {blockDeclared, blockSuccessful}; //if no one blocks, then treat it same as if a block was not successful
    }

    public static void loseInfluence(Player player, Deck deck) {
        if (player.isAlive()) { //don't do anything if the player is already dead.
            int cardIndex = 0;

            //gets player choice on which card to reveal if they have more than one card.
            if(player.getInfluence() == 2) {
                Tools.showOnlyMessage(player.getName() + " will have to choose a card to discard. If this is you, press enter to continue", 0.5);
                Tools.input.nextLine();
                String prompt = "Type '1' to lose your " + player.getHand().get(0).getName() + " or type '2' to lose your " + player.getHand().get(1).getName() + ": ";
                String[] validInputs = {"1", "2"};
                cardIndex = Integer.parseInt(Tools.promptInput(prompt, "Sorry, I didn't understand that. " + prompt, validInputs)) - 1;
            }

            player.loseInfluence(cardIndex, deck);

            if (!player.isAlive()) Tools.showMessage(player.getName() + " is out of the game!", 1.1);
            else Tools.showMessage(player.getName() + " has 1 influence left!\n", 1.75);

            System.out.print("\nPress enter to continue.");
            Tools.input.nextLine();
        }
    }

    public static Player startRound(String roundWinner) {
        resetActivePlayers(); //Puts all registered players into active players list and reset their fields.
        distributeCards(); //Distributes cards to each player and shows them their hand
        return findStartingPlayer(roundWinner); //Figures out who the first player will be.
    }

    private static void distributeCards() {
        deck.shuffle(); //shuffle the deck.
        for(Player player : PLAYERS) { //Each player draws two cards;
            player.drawCard(deck);
            player.drawCard(deck);
            showCards(player);
        }
    }

    private static Player findStartingPlayer(String roundWinner) {
        Player activePlayer;
        if (roundWinner == null) {
            double rand = Math.random()*Game.ALL_PLAYERS.size();
            activePlayer = Game.findPlayerByNum((int)(Math.floor(rand)));
        }
        else activePlayer = findAnyPlayer(roundWinner);
        return activePlayer;
    }

    //DEBUG/TESTING METHODS
    public static void autoStart(int numPlayers) {
        ALL_PLAYERS.clear();
        String[] playerNames = {"Alejandro", "Jolana", "Ali", "Kushy", "Martin", "Jamal"};
        for (int i = 0; i < numPlayers; i++) {
            Game.registerPlayer(playerNames[i], i);
        }
        resetActivePlayers();
        deck.shuffle(); //shuffle the deck.
        for(Player player : PLAYERS) { //Each player draws two cards;
            player.drawCard(deck);
            player.drawCard(deck);
        }
    }
}
