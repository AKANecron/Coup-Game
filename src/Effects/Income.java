package Effects;

import Game.Player;

public class Income extends Effect {
    
    public Income() {
        super("income", false, false, false, 0);
    }
    
    public void execute(Player player){
        player.addCoins(1);
    }
    

    public String toString(){
        return "has collected income!";
    }
}
