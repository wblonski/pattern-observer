package pl.wblonski.patterns.observer;

import pl.wblonski.patterns.observer.exceptions.MyUpdateFailedException;
import pl.wblonski.patterns.observer.util.MyLogger;

import javax.validation.constraints.NotNull;

import static java.lang.String.format;

public class ConcreteObserver implements Observer {
    private String name;
    private Subject subjectRef;
    private State mySubjectStateCopy;

    public ConcreteObserver() {
    }

    public ConcreteObserver(String observerName) {
        this();
        this.name = observerName;
    }

    /*
        @NotNull - ponieważ założenie, że ta metoda wywoływana jest w praktyce wyłącznie z wnętrza obiektu ConcreteSubject
         */
    @Override
    public void setSubjectRef(@NotNull Subject subjectRef) {
        this.subjectRef = subjectRef;
    }

    /*
        Metoda nie rzuca żadnego wyjątku do Podmiotu, ani nie zwraca żadnej informacji zwrotnej,
        cokolwiek by się stało podczas obsługi powiadomienia.
        Podmiotu nie interesuje, czy sobie radzisz lub nie radzisz z obsługą powiadomienia, ani w jaki sposób to robisz.
     */
    @Override
    public void update() {
        try {
            MyLogger.trace(format("Update is starting for %s.", name));
            myUpdate();
        } catch (Exception ex) {

            MyLogger.trace(format("myUpdate() failed: %s", ex.getMessage()));
        }
    }

    private void setMySubjectStateCopy(State mySubjectStateCopy) {
        this.mySubjectStateCopy = mySubjectStateCopy;
    }

    /*
        Metoda zwraca true, jeśli została wykonana reakcja biznesowa obserwatora na powiadomienie.
     */
    private Boolean myUpdate() throws MyUpdateFailedException {
        MyLogger.trace(format("My name is %s. myUpdate is starting now:", name));
        try {
            setMySubjectStateCopy(this.subjectRef.getCurrentState());

            // jeśli obiektem promocji jest rower, to zareaguj entuzjastycznie
            // w przeciwnym razie zignoruj powiadomienie
            if (this.mySubjectStateCopy.productName().equals("Bicycle")) {
                MyLogger.trace(format("- Great, bike is the special offer at \"%s\" today for the price $%s only, expired on %s.",
                        this.mySubjectStateCopy.subjectName(), this.mySubjectStateCopy.tariff(),
                        this.mySubjectStateCopy.expirationDate()));
                return true;
            }
            return false;
        } catch (RuntimeException ex) {
            throw new MyUpdateFailedException(ex);
        }

    }

}
