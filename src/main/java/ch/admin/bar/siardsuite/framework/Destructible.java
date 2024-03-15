package ch.admin.bar.siardsuite.framework;

/**
 * An interface representing objects that can be destructed.
 * Objects implementing this interface can perform a destruct operation.
 */
public interface Destructible {

    /**
     * Performs the destruction operation.
     * Implementing classes should define the behavior of destruction.
     */
    void destruct();
}
