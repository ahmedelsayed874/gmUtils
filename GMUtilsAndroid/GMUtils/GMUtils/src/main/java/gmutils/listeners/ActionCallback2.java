package gmutils.listeners;


import gmutils.collections.values.Value;

/**
 *
 * @param <Args1> you can use {@link Value} to represent multiple arguments or use whatever you want
 * @param <Return>
 */
public interface ActionCallback2<Args1, Args2, Return> {
    Return invoke(Args1 input1, Args2 input2);
}
