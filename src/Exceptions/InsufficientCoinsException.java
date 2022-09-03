package Exceptions;

public final class InsufficientCoinsException extends Exception {
    public InsufficientCoinsException(){
        super("Insufficient coins to perform this effect.");
    }
}
