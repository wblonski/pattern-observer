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
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
    Uwaga: W poniższych testach są być może naużywane wyjątki. Ale to tylko testy.
    Na produkcji częste rzucanie wyjatków na pewno osłabiło by wydajność.
    Jednak w praktyce na produkcji te sytuacje pojawią się bardzo rzadko.
    Mimo to trzeba je uwzględnić w kodzie.
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ObserverSimpleUnitTest {

    private static final String NULL_OBSERVER_REGISTER_EX_MSG = "Próba rejestracji obserwatora \"null\". ";
    private static final String TWICE_OBSERVER_REGISTER_EX_MSG = "Próba ponownej rejestracji już zarejestrowanego obserwatora.";
    private static final String NULL_OBSERVER_UNREGISTER_EX_MSG = "Próba wyrejestrowania obserwatora \"null\".";
    private static final String NOT_EXIST_OBSERVER_UNREGISTER_EX_MSG = "Próba wyrejestrowania niezarejestrowanego obserwatora.";

    @Nested
    @Order(10)
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
        void myUpdateMethodExecutedBusinessAction_WhenSubjectStateIsInteresting()  {
            // given
            ConcreteSubject aShop = new ConcreteSubject("Big Shop");
            Observer anObserver = new ConcreteObserver("Józef Stolarz");
            // when
            try {
                aShop.registerObserver(anObserver);
                // sprawdź, czy wykonała się akcja biznesowa w metodzie myUpdate() obserwatora
                Class<ConcreteObserver> observerClass = ConcreteObserver.class;
                Method myUpadateMethod = observerClass.getDeclaredMethod("myUpdate");
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
        void myUpdateMethodNotExecutedBusinessAction_WhenSubjectStateIsNotInteresting()  {
            // given
            ConcreteSubject aShop = new ConcreteSubject("Big Shop");
            Observer anObserver = new ConcreteObserver("Stanisław Kowal");
            // when
            try {
                aShop.registerObserver(anObserver);
                anObserver.setSubjectRef(aShop);

                Class[] methodParamsTypes;
                // uwtwórz obiekt innej promocji
                Class<ConcreteSubject> claz = ConcreteSubject.class;
                methodParamsTypes = new Class[]{String.class, String.class, Integer.class, LocalDate.class};
                Method method = claz.getDeclaredMethod("createNewSpecialOffer", methodParamsTypes);
                method.setAccessible(true);
                State otherOfferObj = (State) method.invoke(aShop, "Big Shop", "Scutter", 2000, LocalDate.now());

//                // ustaw nową promocję w Podmiocie
                methodParamsTypes = new Class[]{State.class};
                Method changeStateMethod = claz.getDeclaredMethod("changeCurrentSpecialOffer", methodParamsTypes);
                changeStateMethod.setAccessible(true);
                changeStateMethod.invoke(aShop, otherOfferObj);

                // pobierz nową promocję do obserwatora
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