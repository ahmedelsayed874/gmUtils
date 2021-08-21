package gmutils.listeners;


import gmutils.collections.dataGroup.DataGroup1;

/**
 *
 * @param <Args> you can use {@link DataGroup1} to represent multiple arguments or use whatever you want
 * @param <Return>
 */
public interface ActionCallback<Args, Return> {
    Return invoke(Args input);
}
