package gmutils.listeners;


/**
 *
 * @param <Args> you can use {@link gmutils.collections.DataGroup} to represent multiple arguments or use whatever you want
 * @param <Return>
 */
public interface ActionCallback<Args, Return> {
    Return invoke(Args input);
}
