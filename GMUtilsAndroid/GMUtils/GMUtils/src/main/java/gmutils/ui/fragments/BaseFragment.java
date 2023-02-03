package gmutils.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import gmutils.R;
import gmutils.storage.SettingsStorage;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.toast.MyToast;
import gmutils.ui.utils.BaseViewModelObserversHandlers;
import gmutils.ui.utils.ViewSource;
import gmutils.ui.viewModels.BaseViewModel;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
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
                ViewModel viewModel = viewModelProvider.get(viewModelClass);
                viewModels.put(id, viewModel);

                if (viewModel instanceof BaseViewModel) {
                    ((BaseViewModel) viewModel).progressStatusLiveData().observe(this, getProgressStatusLiveData());
                    ((BaseViewModel) viewModel).alertMessageLiveData().observe(this, getAlertMessageLiveData());
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private Observer<BaseViewModel.ProgressStatus> getProgressStatusLiveData() {
        return progressStatus -> {
            if (progressStatus != null)
                onProgressOfViewModelTaskChanged(progressStatus);
        };
    }

    protected void onProgressOfViewModelTaskChanged(BaseViewModel.ProgressStatus progressStatus) {
        new BaseViewModelObserversHandlers().onProgressOfViewModelTaskChanged(
                this.getContext(),
                progressStatus,

                //showWaitView
                this::showWaitView,

                //updateWaitViewMsg
                this::updateWaitViewMsg,

                //hideWaitView
                this::hideWaitView
        );
    }

    private Observer<BaseViewModel.Message> getAlertMessageLiveData() {
        return message -> {
            if (message != null) {
                onMessageReceivedFromViewModel(message);
            }
        };
    }

    protected void onMessageReceivedFromViewModel(BaseViewModel.Message message) {
        new BaseViewModelObserversHandlers().onMessageReceivedFromViewModel(
                this.getContext(),
                message,

                //showMessageDialog,
                m -> listener.showMessageDialog(getContext(), m, null),

                //showToast
                m -> MyToast.show(this.getContext(), m),

                //showRetryPromptDialog
                (m , a) -> showRetryPromptDialog(m, d -> a.run())
        );
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

    public void showWaitView(CharSequence msg) {
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
        showFragment(fragment, new ShowFragmentOptions().setAddToBackStack(false));
    }

    protected void showFragment(Fragment fragment, ShowFragmentOptions options) {
        listenerX.showFragment(fragment, options);
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