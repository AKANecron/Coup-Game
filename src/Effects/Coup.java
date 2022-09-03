package Effects;

import Game.Player;
import Game.Deck;
import Game.Game;

public class Coup extends Effect {
    private final Deck deck;

    public Coup(Deck deck) {
        super("coup", false, false, true, 7);
        this.deck = deck;
    }

    public void execute(Player targetPlayer){
        Game.loseInfluence(targetPlayer, deck);
    }

    public String toString(){
        return "has launched a coup!";
    }
}
