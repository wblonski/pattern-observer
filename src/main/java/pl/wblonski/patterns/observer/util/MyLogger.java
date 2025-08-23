package pl.wblonski.patterns.observer.util;

/*
Klasa narzędziowa z prywatnym konstruktorem.
 */
public class MyLogger {
    private static final String HEADER_MASK_STR = "%n__________ %s __________%n";
    private MyLogger() {}

    public static void trace(String logStr) {
        System.out.println(logStr);
    }

    public static void header(String headerStr) {
        System.out.printf(HEADER_MASK_STR, headerStr);
    }
}
