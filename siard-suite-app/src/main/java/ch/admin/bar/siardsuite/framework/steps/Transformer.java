package ch.admin.bar.siardsuite.framework.steps;

/**
 * A functional interface representing a transformer that converts input data of type {@code TIn} into output data of type {@code TOut}.
 *
 * @param <TIn>  The type of the input data.
 * @param <TOut> The type of the output data.
 */
public interface Transformer<TIn, TOut> {
    /**
     * Transforms the input data into the output data.
     *
     * @param data The input data to be transformed.
     * @return The transformed output data.
     */
    TOut transform(TIn data);
}
