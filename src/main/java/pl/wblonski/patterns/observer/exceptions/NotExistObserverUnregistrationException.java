package pl.wblonski.patterns.observer.exceptions;

public class NotExistObserverUnregistrationException extends Exception {
    public NotExistObserverUnregistrationException() {
    }

    public NotExistObserverUnregistrationException(String message) {
        super(message);
    }
}
