package Effects;

import Exceptions.InsufficientCoinsException;
import Game.Player;

public class Steal extends Effect {
    /**Steals two coins from another player */

    public Steal() {
        super("steal", true, true, true, -2); // Sort of "hacky" way to add two coins to the executing player's bank
    }

    public void execute(Player targetPlayer){
        int coinsTaken = Math.min(2, targetPlayer.getCoins());
        try{targetPlayer.spendCoins(coinsTaken);}
        catch(InsufficientCoinsException e) {e.printStackTrace();} //theoretically should never reach this.
        setCost(-coinsTaken);
    }

    public String toString(){
        return "wants to steal!";
    }
}
