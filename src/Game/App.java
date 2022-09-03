package Game;

import Exceptions.*;
import Effects.*;
import java.util.Collections;
import java.util.Objects;

public class App {

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        //Game initiation logic
        Deck deck = Game.deck;
        Tools.showOnlyMessage("Welcome to JavaCoup!\n", 1.5);
        
        Tools.showOnlyMessage("Before we begin, take a moment to review the short guide below\n", 2.5);
        Tools.showTable();
        Tools.showMessage("\n\n", 5);
        
        Tools.showMessage("Now let's get some players registered. ", 1.5);

        String[] yN = {"y", "n"};
        String[] validNumPlayers = {"3", "4", "5", "6"};
        String rawPlayerCount = Tools.promptInput("Firstly, how many players are there? (Minimum of 3 and maximum of 6): ",
                          "Sorry, that won't work. Please enter a number between 3 and 6: ", 
                          validNumPlayers);
        
        int plyrCount = Integer.parseInt(rawPlayerCount);
        Game.registerPlayers(plyrCount);

        //Rounds loop
        boolean stillPlaying = true;
        String roundWinner = null;
        while(stillPlaying){
            Player activePlayer = Game.startRound(roundWinner);
            //Round Initiation
            roundWinner = null; //reset the roundWinner (if needed) so the turns loop executes.
            
            //Turn logic
            while(roundWinner == null){
                assert activePlayer != null;
                Tools.showOnlyMessage("It is now " + activePlayer.getName() + "'s turn.\n\n", 3);

                String[] validEffects = {"0", "1", "2", "3", "4", "5", "6", "7"};
                int playerChoice = 0;
                boolean sufficientCoins = true; //flips to false if declareEffect throws InsufficientCoinsException
                Effect declaredEffect = Game.EFFECTS[2]; //default to coup until properly overwritten
                if (activePlayer.getCoins() >= 10){ //force a coup if the player has 10 or more coins.
                    for (Player player : Game.PLAYERS) {
                        System.out.println(player.info());
                    }
                }
                else { //otherwise get the input for the effect the player wants to declare.
                    while (playerChoice == 0 || !sufficientCoins) {
                        Tools.showTable();
                        for (Player player : Game.PLAYERS) { //for debugging
                            System.out.println(player.info());
                        }
                        System.out.print("\nDiscard pile:");
                        deck.inspectDiscard();
                        System.out.println();
                        String effectPrompt = "Enter the key number of the effect you wish to declare: ";
                        playerChoice = Integer.parseInt(Tools.promptInput(effectPrompt, "Sorry, that isn't valid. " + effectPrompt, validEffects));
                        try {
                            declaredEffect = activePlayer.declareEffect(playerChoice);
                            sufficientCoins = true;
                        }
                        catch (InsufficientCoinsException e) {
                            Tools.showMessage("Sorry, you don't have enough coins to do that.\n", 1.25);
                            Tools.showMessage("Let's try again.\n", 0.5);
                            Tools.showOnlyMessage("",0); //clear the console before showing the info again.
                            sufficientCoins = false;
                        }
                        catch (ArrayIndexOutOfBoundsException e){ //This gets triggered if player enters 0 - checks hand.
                            Game.showCards(activePlayer);
                        }
                    }
                }
                Tools.showMessage(activePlayer.getName() + " has declared " + declaredEffect.getName() + "!\n", 2.5);
                
                int cardFlag = Game.checkEffectBluff(activePlayer, declaredEffect.getName());
                boolean apIsBluffing = cardFlag < 0; //sentinel value -1 returned from checkEffectBluff if AP bluffed.

                Player targetPlayer = Game.setTargetPlayer(activePlayer, declaredEffect);

                boolean challengeIssued = false;
                boolean challengeSuccessful = false;
                
                if (declaredEffect.isRefutable()){
                    boolean[] challengeResults = Game.processChallenges(activePlayer, apIsBluffing, cardFlag);
                    challengeIssued = challengeResults[0];
                    challengeSuccessful = challengeResults[1];
                }

                boolean blockSuccessful = false;
                if (!challengeIssued && declaredEffect.isBlockable()){ //blocking only relevant when a challenge was not issued.
                    if (declaredEffect instanceof ForeignAid) {
                        for (Player player : Game.findWaitingPlayers(activePlayer)) {
                            boolean[] result = Game.processBlock(activePlayer, player, declaredEffect);
                            blockSuccessful = result[1];
                            if (result[0]) break; //if a block was attempted, stop looping regardless of outcome
                        }
                    }
                    else blockSuccessful = Game.processBlock(activePlayer, targetPlayer, declaredEffect)[1];
                }

                if (!challengeSuccessful && !blockSuccessful) {//both the challenge and the block have to be passed in order to execute.
                    declaredEffect.execute(targetPlayer); //do the effect to the target player (which is the active player if effect is not targeted)
                    try{activePlayer.spendCoins(declaredEffect.getCost());} //spend the coins
                    catch(InsufficientCoinsException e){e.printStackTrace();} //theoretically shouldn't trigger since we check before declaration.
                }
                
                else if (blockSuccessful && declaredEffect instanceof Assassinate){ //blocking assassinate still spends the coins
                    try{activePlayer.spendCoins(3);}
                    catch(InsufficientCoinsException e){e.printStackTrace();} //theoretically shouldn't trigger since we check before declaration.
                }
                
                Game.updatePlayerList();
                roundWinner = Game.getWinner();
                if(!(roundWinner == null)) Objects.requireNonNull(Game.findAnyPlayer(roundWinner)).increaseScore();
                else {activePlayer = Game.findTurnPlayer(activePlayer);}
            }
            
            String cont = Tools.promptInput("Would you like to play another round [y/n]: ",
                                        "Sorry, I didn't get that. Would you like to play another round [y/n]",
                                        yN);
            stillPlaying = cont.equalsIgnoreCase("y");
            deck.resetDeck();
            Game.resetActivePlayers();
        }
        
        // Show the player ranks.
        Collections.sort(Game.ALL_PLAYERS);
        Tools.showOnlyMessage("Thanks for playing! Here are the winners...\n", 3);
        for (int i = Game.ALL_PLAYERS.size(); i > 0; i--) {
            Tools.showMessage("\n# " + i + ".", 0.5);
            Tools.showMessage(".", 0.5);
            Tools.showMessage(". ", 0.5);
            Tools.showMessage(Game.ALL_PLAYERS.get(i-1).getName() + "!", 2);
        }
    }
}
