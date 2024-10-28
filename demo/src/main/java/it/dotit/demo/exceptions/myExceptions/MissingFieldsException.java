package it.dotit.demo.exceptions.myExceptions;

public class MissingFieldsException extends RuntimeException {
    public MissingFieldsException(String message) {
        super(message);
    }
}