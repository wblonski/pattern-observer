package pl.wblonski.patterns.observer.exceptions;

public class TwiceObserverRegistrationException extends Exception {
    public TwiceObserverRegistrationException() {
    }

    public TwiceObserverRegistrationException(String message) {
        super(message);
    }
}
