package gmutils.listeners;


/**
 *
 * @param <Args1> you can use {@link gmutils.collections.DataGroup} to represent multiple arguments or use whatever you want
 * @param <Return>
 */
public interface ActionCallback2<Args1, Args2, Return> {
    Return invoke(Args1 input1, Args2 input2);
}
