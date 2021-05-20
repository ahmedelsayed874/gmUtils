package gmutils.listeners;


public interface ActionCallback<Args, Return> {
    Return invoke(Args input);
}
