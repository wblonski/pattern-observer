package pl.wblonski.patterns.observer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.wblonski.patterns.observer.exceptions.NullObserverRegistrationException;
import pl.wblonski.patterns.observer.exceptions.TwiceObserverRegistrationException;
import pl.wblonski.patterns.observer.util.MyLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcreteObserverTest {

    @BeforeAll
    static void logHeader() {
        MyLogger.header(ConcreteObserverTest.class.getSimpleName());
    }

    @Test
    void fieldSubjectRefIsSet_WhenMethodSetSubjectRefIsCalled() {
        // given
        ConcreteSubject aShop = new ConcreteSubject("Any Shop");
        Observer anObserver = new ConcreteObserver("John Wayne");
        try {
            // when
            anObserver.setSubjectRef(aShop);
            // then
            try {
                // sprawdź, czy metoda setSubjectRef() zmieniła prywatne pole w obiekcie anObserver
                Class<ConcreteObserver> observerClass = ConcreteObserver.class;
                Field field = observerClass.getDeclaredField("subjectRef");
                field.setAccessible(true);
                Subject subiect = (Subject) field.get(anObserver);
                assertEquals(aShop, subiect);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException ex) {
            Assertions.fail(ex.getCause().getMessage() + ":%n " + ex.getMessage());
        }
    }

    @Test
    void myUpdateMethodExecutedBusinessAction_WhenSubjectStateIsInteresting() {
        // given
        ConcreteSubject aShop = new ConcreteSubject("Big Shop");
        Observer anObserver = new ConcreteObserver("Józef Stolarz");
        // when
        try {
            aShop.registerObserver(anObserver);
            // pobierz aktualny stan podmiotu do obserwatora
            State currOfferObj = aShop.getCurrentState();

            // ustaw kopię aktualnego stanu podmiotu w obserwatorze
            // setter setMySubjectStateCopy jest prywatny
            Class<?>[] methodParamsTypes = new Class[]{State.class};
            Method mySetStateCopy = anObserver.getClass().getDeclaredMethod("setMySubjectStateCopy", methodParamsTypes);
            mySetStateCopy.setAccessible(true);
            mySetStateCopy.invoke(anObserver, currOfferObj);

            // sprawdź, czy wykonała się akcja biznesowa w metodzie myUpdate() obserwatora
            Method myUpadateMethod = anObserver.getClass().getDeclaredMethod("myUpdate");
            myUpadateMethod.setAccessible(true);
            Boolean isExecuted = (Boolean) myUpadateMethod.invoke(anObserver);
            // then
            Assertions.assertEquals(true, isExecuted);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 TwiceObserverRegistrationException | NullObserverRegistrationException |
                 RuntimeException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void myUpdateMethodNotExecutedBusinessAction_WhenSubjectStateIsNotInteresting() {
        // given
        ConcreteSubject aShop = new ConcreteSubject("Big Shop");
        Observer anObserver = new ConcreteObserver("Stanisław Kowal");
        // when
        try {
            aShop.registerObserver(anObserver);
            anObserver.setSubjectRef(aShop);

            Class<?>[] methodParamsTypes;
            //
            // utwórz nowę nieinteresującą ofertę jako stan podmiotu
            Class<ConcreteSubject> claz = ConcreteSubject.class;
            methodParamsTypes = new Class[]{String.class, String.class, Integer.class, LocalDate.class};
            Method method = claz.getDeclaredMethod("createNewSpecialOffer", methodParamsTypes);
            method.setAccessible(true);
            State otherOfferObj = (State) method.invoke(aShop, "Big Shop", "Scutter", 2000, LocalDate.now());

            // ustaw utworzoną ofertę w podmiocie, bezpośrednio setterem, a nie przez changeCurrentSpecialOffer
            methodParamsTypes = new Class[]{State.class};
            Method setOfferMethod = claz.getDeclaredMethod("setCurrentSpecialOffer", methodParamsTypes);
            setOfferMethod.setAccessible(true);
            setOfferMethod.invoke(aShop, otherOfferObj);

            // ustaw nową ofertę w obserwatorze prywatnym setterem
            Class<ConcreteObserver> observerClass = ConcreteObserver.class;
            methodParamsTypes = new Class[]{State.class};
            Method mySetStateCopy = observerClass.getDeclaredMethod("setMySubjectStateCopy", methodParamsTypes);
            mySetStateCopy.setAccessible(true);
            mySetStateCopy.invoke(anObserver, otherOfferObj);

            // sprawdź, czy wykonała się akcja biznesowa w metodzie myUpdate() obserwatora
            Method myUpdateMethod = observerClass.getDeclaredMethod("myUpdate");
            myUpdateMethod.setAccessible(true);
            Boolean isExecuted = (Boolean) myUpdateMethod.invoke(anObserver);
            // then
            Assertions.assertEquals(false, isExecuted);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 TwiceObserverRegistrationException | NullObserverRegistrationException |
                 RuntimeException e) {
            Assertions.fail();
        }
    }

    @Test
    void updateWorksOK_WhenMyUpdateFailed() {
        // given
        Observer anObserver = new ConcreteObserver("Tom Smith");
        // when
        // null spowoduje błąd w metodzie prywatnej myUpdate()
        anObserver.setSubjectRef(null);
        try {
            anObserver.update();
            // then
            // OK
        } catch (RuntimeException e) {
            Assertions.fail();
        }
    }
}
