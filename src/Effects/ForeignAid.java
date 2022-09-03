package Effects;

import Game.Player;

public class ForeignAid extends Effect {
    
    public ForeignAid() {
        super("foreign aid", false, true, false, 0);
    }

    public void execute(Player player) {
        player.addCoins(2);
    }
    

    public String toString(){
        return "wants to collect foreign aid!";
    }
}