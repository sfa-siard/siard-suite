package ch.admin.bar.siardsuite.framework.steps;

/**
 * Represents a navigator for a stepper, allowing navigation to the next and previous steps.
 *
 * @param <T> The type of data which is needed to enter the next step.
 */
public interface StepperNavigator<T> {
    /**
     * Navigates to the next step in the stepper.
     *
     * @param data The data to be used in the next step.
     */
    void next(T data);

    /**
     * Navigates to the previous step in the stepper.
     */
    void previous();
}
