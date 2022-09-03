package Game;
import Exceptions.InvalidNameException;


public class Card {
    public static final String[] validNames = {"Ambassador", "Assassin", "Captain", "Contessa", "Duke"};
    private final String name;
    private String effect;
    private String counteraction;

    public Card (String name) throws InvalidNameException{
        if(isValidName(name)) {
            this.name = name;
            switch (name) {
                case "Ambassador" -> {
                    this.effect = "exchange";
                    this.counteraction = "block steal";
                }
                case "Assassin" -> {
                    this.effect = "assassinate";
                    this.counteraction = "N/A";
                }
                case "Captain" -> {
                    this.effect = "steal";
                    this.counteraction = "block steal";
                }
                case "Contessa" -> {
                    this.effect = "N/A";
                    this.counteraction = "block assassinate";
                }
                case "Duke" -> {
                    this.effect = "tax";
                    this.counteraction = "block foreign aid";
                }
            }
        }
        else throw new InvalidNameException();
    }

    //private method for use in the constructor
    private boolean isValidName(String name){
        for(String validName : validNames) {
            if(name.equals(validName)){
                return true;
            }
        }
        return false;
    }

    public String getName() {return this.name;}

    public String getEffect(){return this.effect;}

    public String getCounteraction(){return this.counteraction;}

    public String toString() {
        return "Name: " + this.name + "\n"
             + "Effect: " + this.effect + "\n"
             + "Counteraction: " + this.counteraction + "\n";
    }

}
