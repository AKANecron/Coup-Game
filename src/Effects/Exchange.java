package Effects;

import Game.*;
import java.util.ArrayList;

public class Exchange extends Effect {
    private final Deck deck;
    
    public Exchange(Deck deck) {
        super("exchange", true, false, false, 0);
        this.deck = deck;
    }

    private int findCard(String name, ArrayList<Card> cardList){
        /*
         * Gets the index of a card in a given list based on its name.
         * Uses linear search so returns the first such instance if there are more than one.
         * If no such card exists in the list, returns -1 sentinel value.
         */
        if (cardList.size() > 0) {
            for (int i = 0; i < cardList.size(); i++){
                if(cardList.get(i).getName().equalsIgnoreCase(name)) {return i;}
            }
            return -1; //if the name is not in the list
        }
        return -1; //if the given list has no elements in it
    }

    private void swapCards(ArrayList<Card> newCards, String newCard, ArrayList<Card> playerHand, String thrownCard) {
        Card exchangedCard = playerHand.get(findCard(thrownCard, playerHand)); //temporary storage for the card to be swapped out.
        int newCardIndex = findCard(newCard, newCards); //get index of card to be swapped in
        playerHand.add(newCards.get(newCardIndex)); //adds swapped-in card to player hand
        int thrownIndex = findCard(thrownCard, playerHand); //get index of card to be swapped out
        playerHand.remove(thrownIndex); //remove the thrown card from the player hand
        newCards.set(newCardIndex, exchangedCard); //put the thrown card in place of the swapped in card in the original newCards list
    }

    public void execute(Player targetPlayer) { // seriously, fuck this effect...
        // Sets up the array lists of cards that will be used.

        ArrayList<Card> newCards = new ArrayList<>(2);
        newCards.add(deck.drawCard());
        newCards.add(deck.drawCard());
        ArrayList<Card> playerHand = targetPlayer.getHand();
        
        // Valid input arrays for downstream string input checking.
        String[] yN = {"y", "n"}; // For any yes/no questions.
        
        String[] yourCards = new String[playerHand.size() + 1]; //will include the names of the player's cards.
        yourCards[yourCards.length - 1] = "pass"; //puts "pass" at the end to allow the player to skip the exchange.

        String[] drawnCards = {newCards.get(0).getName().toLowerCase(), newCards.get(0).getName().toLowerCase(), "pass"};
        
        // User input collection
        System.out.println(targetPlayer.getName() + 
                           " will now perform an exchange. If this is you, press enter to continue."); 
        Tools.input.nextLine(); // this action reveals cards, so first ensure we are on the right player
        
        String keepSwapping = "y"; // let the player keep swapping until they're satisfied.
        while (keepSwapping.equals("y")) {
            
            //shows the player's hand to them and updates the valid inputs array yourCards.
            Tools.showOnlyMessage("Here are the cards in your hand:", 0);
            for (int i = 0; i < playerHand.size(); i++) { 
                String name = playerHand.get(i).getName();
                yourCards[i] = name.toLowerCase();
                System.out.print(" " + name);
            }
            
            //shows the available swapping cards and updates the valid inputs array drawnCards.
            System.out.print("\nHere are the cards you can swap to:");
            for (int i = 0; i < 2; i++) { 
                String name = newCards.get(i).getName();
                drawnCards[i] = name.toLowerCase();
                System.out.print(" " + name);
            }
            Tools.showMessage("\n", 2.5);

            String thrownCardName = Tools.promptInput("Type the name of the card to swap out or type pass to keep your card: ",
                                                    "Sorry, I didn't understand that. Please try again: ", yourCards).toLowerCase();
            if (thrownCardName.equals("pass")){
                System.out.print("We don't want anyone to know you passed, so we'll have to pretend you're typing again. Type anything and press enter: ");
                Tools.input.nextLine();
            }

            else {
                String newCardName = Tools.promptInput("Type the name of the card you want or type pass to keep your card: ",
                                                    "Sorry, I didn't understand that. Please try again: ", drawnCards).toLowerCase();
                if (!(newCardName.equals("pass"))) swapCards(newCards, newCardName, playerHand, thrownCardName);
            }

            keepSwapping = Tools.promptInput("Would you like to swap another card? [y/n]: ",
                                           "Sorry, I didn't understand that. Please try again: ", yN).toLowerCase();
        }
        
        // Since newCards should now contain only things the player didn't want, we can return all of its cards to the deck.
        for (Card card : newCards) {
            deck.returnCard(card);
        }
        Tools.clearConsole(); // naturally, the console needs to be cleared at the end of all of this.
    }

    public String toString(){
        return "wants to perform an exchange!";
    }
}
