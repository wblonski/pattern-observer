package pl.wblonski.patterns.observer.exceptions;

public class NullObserverUnregistrationException extends Exception {
    public NullObserverUnregistrationException() {
    }

    public NullObserverUnregistrationException(String message) {
        super(message);
    }
}