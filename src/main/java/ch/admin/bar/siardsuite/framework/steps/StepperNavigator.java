package ch.admin.bar.siardsuite.framework.steps;

public interface StepperNavigator<T> {
    void next(T data);
    void previous();
}
