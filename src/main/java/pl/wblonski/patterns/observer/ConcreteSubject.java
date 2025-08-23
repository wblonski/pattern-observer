package pl.wblonski.patterns.observer;

import pl.wblonski.patterns.observer.exceptions.NotExistObserverUnregistrationException;
import pl.wblonski.patterns.observer.exceptions.NullObserverRegistrationException;
import pl.wblonski.patterns.observer.exceptions.NullObserverUnregistrationException;
import pl.wblonski.patterns.observer.exceptions.TwiceObserverRegistrationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConcreteSubject implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private final String subjectName;
    private State currentSpecialOffer;

    public ConcreteSubject(String subjectName) {
        this.subjectName = subjectName;
        currentSpecialOffer = new MyState(this.subjectName, "Bicycle", 1000, LocalDate.now().plusDays(7));
    }

    @Override
    public void registerObserver(Observer observerObj) throws TwiceObserverRegistrationException, NullObserverRegistrationException {
        if (observerObj == null) {
            throw new NullObserverRegistrationException();
        } else if (observers.contains(observerObj)) {
            throw new TwiceObserverRegistrationException();
        } else {
            observers.add(observerObj);
            observerObj.setSubjectRef(this);
        }
    }

    @Override
    public void unregisterObserver(Observer observerObj) throws NullObserverUnregistrationException, NotExistObserverUnregistrationException {
        if (observerObj == null) {
            throw new NullObserverUnregistrationException();
        } else if (!observers.contains(observerObj)) {
            throw new NotExistObserverUnregistrationException();
        } else {
            observers.remove(observerObj);
        }
    }

    @Override
    public State getCurrentState() {
        return currentSpecialOffer;
    }

    private void setCurrentSpecialOffer(State currentSpecialOffer) {
        this.currentSpecialOffer = currentSpecialOffer;
    }

    public void notifyObservers() {
        // na każdym obiekcie listy wykonaj synchronicznie update
        // bez informacji zwrotnej
        for (Observer observer : observers) {
            observer.update();
        }
    }

    private void changeCurrentSpecialOffer(State newState) {
        this.currentSpecialOffer = newState;
        notifyObservers();
    }

    private State createNewSpecialOffer(String subjectName, String productName, Integer tariff, LocalDate expirationDate) {
        return new MyState(subjectName, productName, tariff, expirationDate);
    }

    private record MyState(String subjectName, String productName, Integer tariff, LocalDate expirationDate)
            implements State {
    }

}
