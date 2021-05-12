package com.blogspot.gm4s1.gmutils.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils.ui.utils.ViewSource;
import com.blogspot.gm4s1.gmutils.ui.viewModels.BaseViewModel;
import com.blogspot.gm4s1.gmutils.ui.dialogs.RetryPromptDialog;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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
    private Listener listener = null;
    private HashMap<Integer, BaseViewModel> viewModels;

    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NotNull
    protected abstract ViewSource getViewReference();

    protected abstract int getFragmentLayout();

    @Nullable
    protected abstract View getFragmentView(LayoutInflater inflater, ViewGroup container, boolean attachToRoot);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewSource viewSource = getViewReference();
        assert viewSource != null;
        View view;

        switch (viewSource) {
            case LayoutResource:
                view = inflater.inflate(getFragmentLayout(), container, false);
                break;

            case ViewObject:
                view = getFragmentView(inflater, container, false);
                break;

            default:
                view = null;
                break;
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.onFragmentStarted(this);
    }

//----------------------------------------------------------------------------------------------

    protected HashMap<Integer, Class<? extends BaseViewModel>> getViewModelClasses() {
        return null;
    }

    protected ViewModelProvider.Factory onCreateViewModelFactory(int id) {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication());
        return viewModelFactory;
    }

    public BaseViewModel getViewModel() {
        if (viewModels.size() == 1) {
            return viewModels.values().toArray(new BaseViewModel[0])[0];
        }

        throw new IllegalStateException("You have declare several View Models in getViewModelClasses()");
    }

    public BaseViewModel getViewModel(int id) {
        return viewModels.get(id);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //------------------------------------------------------------------------------------------

        HashMap<Integer, Class<? extends BaseViewModel>> viewModelClasses = getViewModelClasses();
        if (viewModelClasses != null) {
            viewModels = new HashMap<>();

            for (Integer id : viewModelClasses.keySet()) {
                ViewModelProvider viewModelProvider = new ViewModelProvider(
                        this,
                        onCreateViewModelFactory(id)
                );

                Class<? extends BaseViewModel> viewModelClass = viewModelClasses.get(id);
                assert viewModelClass != null;
                viewModels.put(id, viewModelProvider.get(viewModelClass));
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    public void setKeyboardAutoHidden() {
        listener.setKeyboardAutoHidden();
    }

    //----------------------------------------------------------------------------------------------

    public Listener getListener() {
        return listener;
    }


    //----------------------------------------------------------------------------------------------

    private int waitViewShowCount = 0;

    public void showWaitView() {
        showWaitView(R.string.wait_moments);
    }

    public void showWaitView(int msg) {
        if (waitViewShowCount == 0) onWaitViewWillShow(msg);
        waitViewShowCount++;
    }

    public void hideWaitView() {
        if (waitViewShowCount == 1) onWaitViewWillHide();
        waitViewShowCount--;
        if (waitViewShowCount < 0) waitViewShowCount = 0;
    }

    public void onWaitViewWillShow() {
        onWaitViewWillShow(R.string.wait_moments);
    }

    public void onWaitViewWillShow(int msg) {
        listener.showWaitView(msg);
    }

    public void onWaitViewWillHide() {
        listener.hideWaitView();
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
        return listener.showRetryPromptDialog(msg, onRetry, onCancel);
    }

    //----------------------------------------------------------------------------------------------

    protected void showFragment(Fragment fragment) {
        showFragment(fragment, false, null, null);
    }

    protected void showFragment(Fragment fragment, boolean addToBackStack, @Nullable Integer fragmentContainerId) {
        listener.showFragment(fragment, addToBackStack, fragment.getClass().getName(), fragmentContainerId);
    }

    protected void showFragment(Fragment fragment, boolean addToBackStack, @Nullable String stackName, @Nullable Integer fragmentContainerId) {
        listener.showFragment(fragment, addToBackStack, stackName, fragmentContainerId);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModels != null) viewModels.clear();
        hideWaitView();
    }


    //----------------------------------------------------------------------------------------------

    public interface Listener {
        void setKeyboardAutoHidden();

        void showWaitView(int msg); //R.string.wait_moments

        void hideWaitView();

        void updateWaitViewMsg(CharSequence msg);

        RetryPromptDialog showRetryPromptDialog(
                CharSequence msg,
                RetryPromptDialog.Listener onRetry,
                RetryPromptDialog.Listener onCancel
        );

        void showFragment(Fragment fragment, boolean addToBackStack, @Nullable String stackName, @Nullable Integer fragmentContainerId);

        void onFragmentStarted(BaseFragment fragment);
    }
}