package ch.admin.bar.siardsuite.framework.steps;

public interface Transformer<TIn, TOut> {
    TOut transform(TIn data);
}
