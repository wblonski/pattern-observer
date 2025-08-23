package pl.wblonski.patterns.observer;

import org.junit.jupiter.api.*;
import pl.wblonski.patterns.observer.exceptions.NotExistObserverUnregistrationException;
import pl.wblonski.patterns.observer.exceptions.NullObserverRegistrationException;
import pl.wblonski.patterns.observer.exceptions.NullObserverUnregistrationException;
import pl.wblonski.patterns.observer.exceptions.TwiceObserverRegistrationException;
import pl.wblonski.patterns.observer.util.MyLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
    Uwaga: W poniższych testach są być może naużywane wyjątki. Ale to tylko testy.
    Na produkcji częste rzucanie wyjatków na pewno osłabiło by wydajność.
    Jednak w praktyce na produkcji te sytuacje pojawią się bardzo rzadko.
    Mimo to trzeba je uwzględnić w kodzie.
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ConcreteSubjectTest {

    @Nested
    @Order(20)
    class ConcreteSubjectPositivePathTest {
        @BeforeAll
        static void logHeader() {
            MyLogger.header(ConcreteSubjectPositivePathTest.class.getSimpleName());
        }

        @Test
        void noProblem_whenAnyObserverIsProperlyRegisteredOrUnregistered() {
            // given
            try {
                ConcreteSubject aShop = new ConcreteSubject("Green Shop");
                Observer anObserver = new ConcreteObserver("Jan Kowalski");
                try {
                    // when
                    aShop.registerObserver(anObserver);
                    aShop.unregisterObserver(anObserver);
                    // then
                    // OK
                } catch (NullObserverRegistrationException | TwiceObserverRegistrationException |
                         NullObserverUnregistrationException | NotExistObserverUnregistrationException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + ":%n " + ex.getMessage());
            }
        }

        @Test
        void allOK_whenStandardClientWorks() {
            try {
                // given
                Subject greenShop = new ConcreteSubject("Green Shop");
                Observer observer1 = new ConcreteObserver("Black Jack");
                Observer observer2 = new ConcreteObserver("Jan Kowalski");
                Observer observer3 = new ConcreteObserver("Zofia Nowak");
                try {
                    // when
                    greenShop.registerObserver(observer1);
                    greenShop.registerObserver(observer2);
                    greenShop.registerObserver(observer3);
                    greenShop.unregisterObserver(observer2);

                    // reflecton: call private method from greenShop: notifyObservers()
                    Class<ConcreteSubject> claz = ConcreteSubject.class;
                    Method method = claz.getDeclaredMethod("notifyObservers");
                    method.setAccessible(true);
                    method.invoke(greenShop);
                    // then
                    // OK
                } catch (NullObserverRegistrationException | TwiceObserverRegistrationException |
                         NullObserverUnregistrationException | NotExistObserverUnregistrationException |
                         NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + " : " + ex.getMessage());
            }
        }

        @Test
        void isOneObserverOnList_whenIsRegisteredFirstTime() {
            try {
                // given
                ConcreteSubject aShop = new ConcreteSubject("Green Shop");
                Observer anObserver = new ConcreteObserver("Jan Kowalski");
                try {
                    // when
                    aShop.registerObserver(anObserver);
                    // sprawdź, czy metoda registerObserver() dodała obserwatora
                    // do prywatnej listy obserwatorów w obiekcie sShop
                    Class<ConcreteSubject> claz = ConcreteSubject.class;
                    Field field = claz.getDeclaredField("observers");
                    field.setAccessible(true);
                    Object observersList = field.get(aShop);
                    int counter = 0;
                    if (observersList instanceof ArrayList) {
                        for (Observer observer : (ArrayList<Observer>) observersList) {
                            if (observer.equals(anObserver)) {
                                counter++;
                            }
                        }
                    }
                    // then
                    assertEquals(1, counter);
                } catch (NullObserverRegistrationException | TwiceObserverRegistrationException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + " : " + ex.getMessage());
            }
        }
    }

    @Nested
    @Order(30)
    class ConcreteSubjectNegativePathTest {

        private static final String NULL_OBSERVER_REGISTER_EX_MSG = "Próba rejestracji obserwatora \"null\". ";
        private static final String TWICE_OBSERVER_REGISTER_EX_MSG = "Próba ponownej rejestracji już zarejestrowanego obserwatora.";
        private static final String NULL_OBSERVER_UNREGISTER_EX_MSG = "Próba wyrejestrowania obserwatora \"null\".";
        private static final String NOT_EXIST_OBSERVER_UNREGISTER_EX_MSG = "Próba wyrejestrowania niezarejestrowanego obserwatora.";

        @BeforeAll
        static void logHeader() {
            MyLogger.header(ConcreteSubjectNegativePathTest.class.getSimpleName());
        }

        @Test
        void throwException_whenNullObserverIsRegistering() {
            // given
            try {
                ConcreteSubject aShop = new ConcreteSubject("Green Shop");
                try {
                    // when
                    aShop.registerObserver(null);
                    // then
                    throw new RuntimeException();
                } catch (NullObserverRegistrationException e) {
                    MyLogger.trace(NULL_OBSERVER_REGISTER_EX_MSG + " - poprawnie odrzucona");
                    // OK
                } catch (TwiceObserverRegistrationException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + " : " + ex.getMessage());
            }
        }

        @Test
        void throwException_whenObserverIsRegisteredTWice() {
            // given
            try {
                ConcreteSubject aShop = new ConcreteSubject("Green Shop");
                Observer anObserver = new ConcreteObserver("Jan Kowalski");
                try {
                    // when
                    aShop.registerObserver(anObserver);
                    aShop.registerObserver(anObserver);
                    // then
                    throw new RuntimeException(TWICE_OBSERVER_REGISTER_EX_MSG);
                } catch (NullObserverRegistrationException e) {
                    throw new RuntimeException(e);
                } catch (TwiceObserverRegistrationException e) {
                    MyLogger.trace(TWICE_OBSERVER_REGISTER_EX_MSG + " - poprawnie odrzucona");
                    // OK
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + " : " + ex.getMessage());
            }
        }

        @Test
        void throwException_whenNullObserverIsUnregistering() {
            // given
            try {
                ConcreteSubject aShop = new ConcreteSubject("Green Shop");
                try {
                    // when
                    aShop.unregisterObserver(null);
                    // then
                    throw new RuntimeException();
                } catch (NullObserverUnregistrationException e) {
                    MyLogger.trace(NULL_OBSERVER_UNREGISTER_EX_MSG + " - poprawnie odrzucona");
                    // OK
                } catch (NotExistObserverUnregistrationException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + " : " + ex.getMessage());
            }
        }

        @Test
        void throwsException_whenNonExistObserverIsUnegistering() {
            // given
            try {
                ConcreteSubject aShop = new ConcreteSubject("Green Shop");
                Observer anObserver = new ConcreteObserver("Jan Kowalski");
                try {
                    // when
                    aShop.unregisterObserver(anObserver);
                    // then
                    throw new RuntimeException(TWICE_OBSERVER_REGISTER_EX_MSG);
                } catch (NullObserverUnregistrationException e) {
                    throw new RuntimeException(e);
                } catch (NotExistObserverUnregistrationException e) {
                    MyLogger.trace(NOT_EXIST_OBSERVER_UNREGISTER_EX_MSG + " - poprawnie odrzucona");
                }
            } catch (RuntimeException ex) {
                Assertions.fail(ex.getCause().getMessage() + " : " + ex.getMessage());
            }
        }
    }
}