package Exceptions;

public final class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException() {
        super("Player name not found.");
    }
}
