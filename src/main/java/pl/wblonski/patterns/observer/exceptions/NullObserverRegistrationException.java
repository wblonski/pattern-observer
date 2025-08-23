package pl.wblonski.patterns.observer.exceptions;

public class NullObserverRegistrationException extends Exception {
    public NullObserverRegistrationException() {
    }

    public NullObserverRegistrationException(String message) {
        super(message);
    }
}