package Effects;

import Game.Game;
import Game.Player;
import Game.Deck;

public class Assassinate extends Effect{

    private final Deck deck;

    public Assassinate(Deck deck) {
        super("assassinate", true, true, true, 3);
        this.deck = deck;
    }
    
    public void execute(Player targetPlayer){
        Game.loseInfluence(targetPlayer, deck);
    }

    public String toString(){
        return "wants to perform an assassination!";
    }
}
