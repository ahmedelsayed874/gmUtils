package gmutils.listeners;


import gmutils.collections.dataGroup.DataGroup1;

/**
 *
 * @param <Args1> you can use {@link DataGroup1} to represent multiple arguments or use whatever you want
 * @param <Return>
 */
public interface ActionCallback4<Args1, Args2, Args3, Args4, Return> {
    Return invoke(Args1 input1, Args2 input2, Args3 input3, Args4 input4);
}
