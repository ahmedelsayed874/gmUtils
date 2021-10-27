package gmutilssupport.ui.fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;

import gmutilsSupport.R;
import gmutilssupport.ui.dialogs.RetryPromptDialog;
import gmutilssupport.ui.utils.ViewSource;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class BaseFragment extends Fragment {
    private BaseFragmentListener listener = null;
    private BaseFragmentListenerX listenerX = null;
    private HashMap<Integer, ViewModel> viewModels;

    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseFragmentListener) {
            listener = (BaseFragmentListener) context;
        } else {
            Log.e("****", context.getClass().getSimpleName() + " isn't implement " + BaseFragmentListener.class.getSimpleName());
        }

        if (context instanceof BaseFragmentListenerX) {
            listenerX = (BaseFragmentListenerX) context;
        } else {
            Log.e("****", context.getClass().getSimpleName() + " isn't implement " + BaseFragmentListenerX.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        listenerX = null;
    }

    //----------------------------------------------------------------------------------------------

    private ViewBinding fragmentViewBinding;

    @NotNull
    protected abstract ViewSource getViewSource(@NotNull LayoutInflater inflater, ViewGroup container);

    public ViewBinding getFragmentViewBinding() {
        return fragmentViewBinding;
    }

    //----------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewSource viewSource = getViewSource(inflater, container);
        assert viewSource != null;
        View view = null;

        if (viewSource instanceof ViewSource.LayoutResource) {
            int resId = ((ViewSource.LayoutResource) viewSource).getResourceId();
            view = inflater.inflate(resId, container, false);

        } else if (viewSource instanceof ViewSource.View) {
            view = ((ViewSource.View) viewSource).getView();

        } else if (viewSource instanceof ViewSource.ViewBinding) {
            fragmentViewBinding = ((ViewSource.ViewBinding) viewSource).getViewBinding();
            view = fragmentViewBinding.getRoot();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listenerX.onFragmentStarted(this);
    }

//----------------------------------------------------------------------------------------------

    protected HashMap<Integer, Class<? extends ViewModel>> onPreparingViewModels() {
        return null;
    }

    protected ViewModelProvider.Factory onCreateViewModelFactory(int id) {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication());
        return viewModelFactory;
    }

    public ViewModel getViewModel() {
        if (viewModels.size() == 1) {
            return viewModels.values().toArray(new ViewModel[0])[0];
        }

        throw new IllegalStateException("You have declare several View Models in getViewModelClasses()");
    }

    public ViewModel getViewModel(int id) {
        return viewModels.get(id);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //------------------------------------------------------------------------------------------

        HashMap<Integer, Class<? extends ViewModel>> viewModelClasses = onPreparingViewModels();
        if (viewModelClasses != null) {
            viewModels = new HashMap<>();

            for (Integer id : viewModelClasses.keySet()) {
                ViewModelProvider viewModelProvider = new ViewModelProvider(
                        this,
                        onCreateViewModelFactory(id)
                );

                Class<? extends ViewModel> viewModelClass = viewModelClasses.get(id);
                assert viewModelClass != null;
                viewModels.put(id, viewModelProvider.get(viewModelClass));
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setKeyboardAutoHidden() {
        listener.setKeyboardAutoHidden(getActivity());
    }

    //----------------------------------------------------------------------------------------------

    public BaseFragmentListener getListener() {
        return listener;
    }

    public BaseFragmentListenerX getListenerX() {
        return listenerX;
    }

    //----------------------------------------------------------------------------------------------

    public void showWaitView() {
        showWaitView(R.string.wait_moments);
    }

    public void showWaitView(int msg) {
        listener.showWaitView(getContext(), msg);
    }

    public void hideWaitView() {
        listener.hideWaitView();
    }

    public void hideWaitViewImmediately() {
        listener.hideWaitViewImmediately();
    }

    public void updateWaitViewMsg(int msg) {
        listener.updateWaitViewMsg(getString(msg));
    }

    public void updateWaitViewMsg(CharSequence msg) {
        listener.updateWaitViewMsg(msg);
    }

    //----------------------------------------------------------------------------------------------

    public void showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry) {
        showRetryPromptDialog(msg, onRetry, null);
    }

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return listener.showRetryPromptDialog(getActivity(), msg, onRetry, onCancel);
    }

    //----------------------------------------------------------------------------------------------

    protected void showFragment(Fragment fragment) {
        showFragment(fragment, false, null, null);
    }

    protected void showFragment(Fragment fragment, boolean addToBackStack, @Nullable Integer fragmentContainerId) {
        listenerX.showFragment(fragment, addToBackStack, fragment.getClass().getName(), fragmentContainerId);
    }

    protected void showFragment(Fragment fragment, boolean addToBackStack, @Nullable String stackName, @Nullable Integer fragmentContainerId) {
        listenerX.showFragment(fragment, addToBackStack, stackName, fragmentContainerId);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideWaitView();
        fragmentViewBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModels != null) viewModels.clear();
    }

}