package gmutilssupport.ui.fragments;

public class ShowFragmentOptions {
    private boolean addToBackStack;
    private String stackName;
    private Integer fragmentContainerId;
    private Integer transition;

    public boolean isAddToBackStack() {
        return addToBackStack;
    }

    public ShowFragmentOptions setAddToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
        return this;
    }

    public String getStackName() {
        return stackName;
    }

    public ShowFragmentOptions setStackName(String stackName) {
        this.stackName = stackName;
        return this;
    }

    public Integer getFragmentContainerId() {
        return fragmentContainerId;
    }

    public ShowFragmentOptions setFragmentContainerId(Integer fragmentContainerId) {
        this.fragmentContainerId = fragmentContainerId;
        return this;
    }

    public Integer getTransition() {
        return transition;
    }

    public ShowFragmentOptions setTransition(Integer transition) {
        this.transition = transition;
        return this;
    }
}
