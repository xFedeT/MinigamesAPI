package it.fedet.minigames.commands.exception;

public class NotLampCommandClassException extends ClassNotFoundException {

    private static final String MESSAGE = "The class doesn't implement a Lamp Command!";

    public NotLampCommandClassException() {
        super("The class doesn't implement a Lamp Command!");
    }

}
