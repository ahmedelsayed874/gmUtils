package com.blogspot.gm4s1.gmutils._ui._bases.compat;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils._ui.dialogs.RetryPromptDialog;

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
public abstract class BaseCompatFragment extends Fragment {
    private Listener listener = null;

    public BaseCompatFragment() {
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

    protected abstract int getFragmentLayout();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        return view;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    public void showFragment(BaseCompatFragment fragment, String stackName) {
        listener.showFragment(fragment, stackName);
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

        void showFragment(BaseCompatFragment fragment, String stackName);

        void showFragment(BaseCompatFragment fragment, String stackName, int fragmentContainerId);
    }
}