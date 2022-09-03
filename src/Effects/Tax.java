package Effects;

import Game.Player;

public class Tax extends Effect {
    
    public Tax() {
        super("tax", true, false, false, 0);
    }

    public void execute(Player targetPlayer){
        targetPlayer.addCoins(3);
    }

    public String toString(){
        return "wants to collect tax!";
    }
}
