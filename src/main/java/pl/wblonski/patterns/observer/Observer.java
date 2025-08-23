package pl.wblonski.patterns.observer;

import javax.validation.constraints.NotNull;

public interface Observer {
    void setSubjectRef(@NotNull Subject subject);

    void update();
}
