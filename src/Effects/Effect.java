package Effects;

import Game.Player;

public abstract class Effect {
    /**
     * Refutable: A given effect can be challenged (true)
     * Blockable: A given effect has a blocking counteraction (true)
     * Targeted: A given effect targets another player (true)
     * Cost: Number of coins needed to execute the action
     */
    private final String name;
    private final boolean refutable,blockable,targeted;
    private int cost;
    
    public Effect(String name, boolean refutable, boolean blockable, boolean targeted, int cost){
        this.name = name;
        this.refutable = refutable;
        this.blockable = blockable;
        this.targeted = targeted;
        this.cost = cost;
    }

    public abstract void execute(Player targetPlayer);

    //Accessors
    public String getName() {return name;}

    public boolean isRefutable() {return refutable;}

    public boolean isBlockable() {return blockable;}

    public boolean isTargeted() {return targeted;}

    public int getCost() {return cost;}

    public void setCost(int cost) {this.cost = cost;}

    public abstract String toString();
}
