package pl.wblonski.patterns.observer;

import pl.wblonski.patterns.observer.exceptions.NotExistObserverUnregistrationException;
import pl.wblonski.patterns.observer.exceptions.NullObserverRegistrationException;
import pl.wblonski.patterns.observer.exceptions.NullObserverUnregistrationException;
import pl.wblonski.patterns.observer.exceptions.TwiceObserverRegistrationException;

public interface Subject {
    void registerObserver(Observer observer) throws TwiceObserverRegistrationException, NullObserverRegistrationException;

    void unregisterObserver(Observer observer) throws NullObserverUnregistrationException, NotExistObserverUnregistrationException;

    State getCurrentState();

}
