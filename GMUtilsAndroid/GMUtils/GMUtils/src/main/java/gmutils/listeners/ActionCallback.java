package gmutils.listeners;


import gmutils.collections.values.Value;

/**
 *
 * @param <Args> you can use {@link Value} to represent multiple arguments or use whatever you want
 * @param <Return>
 */
public interface ActionCallback<Args, Return> {
    Return invoke(Args input);
}
