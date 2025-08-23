package pl.wblonski.patterns.observer.exceptions;

public class MyUpdateFailedException extends Exception {
    public MyUpdateFailedException() {
    }

    public MyUpdateFailedException(String message) {
        super(message);
    }

    public MyUpdateFailedException(Exception cause) {
        super(cause);
    }
}
