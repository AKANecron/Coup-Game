package Game;

import java.util.ArrayList;

import Effects.*;
import Exceptions.InsufficientCoinsException;

public class Player implements Comparable<Player>{
    private final String name;
    private final int number; //keeps track of this player's index in the list of all registered players
    private final ArrayList<Card> hand = new ArrayList<>();
    private int coins;
    private int influence;
    private boolean alive;
    private int score;

    protected Player(String name, int number) {
        this.name = name;
        this.number = number;
        this.score = 0;
        resetPlayer();
    }

    //Accessors
    public String getName() {return this.name;}

    protected int getNumber() {return this.number;}

    public int getInfluence() {return this.influence;}
    
    public ArrayList<Card> getHand() {
        return this.hand;
    }
    
    public int getCoins() {
        return this.coins;
    }

    private int getScore() {return this.score;}

    protected void increaseScore() {score++;}
    
    public void addCoins(int num) {
        coins += num;
    }

    protected void resetPlayer() {
        coins = 2;
        influence = 2;
        alive = true;
        hand.clear();
    }

    public void spendCoins(int num) throws InsufficientCoinsException{
        if (num <= coins){coins -= num;}
        else throw new InsufficientCoinsException();
    }

    protected void drawCard(Deck deck){
        hand.add(deck.drawCard());
    }

    //KNOWN ISSUE - When a player has both the Ambassador and the Captain, and they block stealing,
    //the game has no way of knowing which card the player would rather swap and will simply
    //pick the first one it finds in their hand. Need to think about how to fix that....
    private void swapCards(Deck deck, int cardIndex) {
        if (cardIndex >= 0 && cardIndex < hand.size()) {
            Card oldCard = hand.get(cardIndex);
            deck.returnCard(oldCard);
            hand.remove(cardIndex);
            deck.shuffle();
            drawCard(deck);
            Tools.showMessage("\n" + this.name + " has swapped cards. Preparing to show new hand...\n", 1.5);
            Game.showCards(this);
        }
    }

    protected Effects.Effect declareEffect(int effect) throws InsufficientCoinsException{
        Effect eff = Game.EFFECTS[effect - 1];
        if (coins >= eff.getCost()) 
            return Game.EFFECTS[effect - 1];
        else throw new Exceptions.InsufficientCoinsException();
        //determine if effect is bluff or not
        //declares effect without executing
    }

    protected boolean challenge(Player targetPlayer, boolean isBluffing, int cardIndex, Deck deck){
        String loser = name;
        if (isBluffing) {
            loser = targetPlayer.getName();
            Tools.showMessage(loser + " was bluffing! ", 1.25);
            Tools.showMessage(loser + " loses influence.", 1);
            Game.loseInfluence(targetPlayer,deck);
            return true; //challenge succeeded
        }
        else {
            Tools.showMessage(targetPlayer.getName() + " wasn't bluffing!\n", 1.25);
            Tools.showMessage(loser + " loses influence.\n", 1);
            Game.loseInfluence(this, deck);
            targetPlayer.swapCards(deck, cardIndex);
            return false; //challenge failed
        }
    }

    protected String counteract(Effect effect){
        return "block " + effect.getName();
    }

    protected void loseInfluence(int cardIndex, Deck deck) {
        if (this.influence > 0) {
            Tools.showOnlyMessage(name + " has discarded their " + hand.get(cardIndex).getName() + "!\n", 3.2);
            influence--; //reduce player influence by 1
            deck.discard(hand.get(cardIndex));
            hand.remove(cardIndex); //remove a chosen card from the player's hand

            if (influence < 1) { //if player influence falls to 0 or less, they are no longer active
                alive = false;
            }
        }
    }

    public int compareTo(Player player) {
        return Integer.compare(player.getScore(), this.score);
    }

    protected String info() {
        if (this.alive) {
            return "Player " + (this.number + 1) + " (" + this.name + ") has "
                   + this.coins + " coins and " + this.influence + " influence."; 
        }
        else {return "Player " + (this.number + 1) + " (" + this.name + ") is exiled.";}
    }

    protected boolean equals(Player player){
        return name.equals(player.name);
    }

    protected boolean isAlive() {return this.alive;}
    
//----------------------------------------------------DEBUG/TESTING ONLY-----------------------------------------------

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setInfluence(int influence) {
        this.influence = influence;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
