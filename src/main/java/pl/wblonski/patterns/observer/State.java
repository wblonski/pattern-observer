package pl.wblonski.patterns.observer;

import java.time.LocalDate;

public interface State {
    String subjectName();

    String productName();

    Integer tariff();

    LocalDate expirationDate();
}
