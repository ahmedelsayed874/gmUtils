package gmutils.ui.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import gmutils.R;
import gmutils.listeners.ResultCallback;
import gmutils.logger.Logger;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.utils.ViewSource;

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
public abstract class BaseLegacyFragment extends Fragment {
    private Listener listener = null;

    public BaseLegacyFragment() {
        super();
    }

    @Override
    public void onAttach(@NotNull Context context) {
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
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
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

    public void hideWaitViewImmediately() {
        listener.hideWaitViewImmediately();
        waitViewShowCount = 0;
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

    public void showFragment(BaseLegacyFragment fragment, String stackName) {
        listener.showFragment(fragment, stackName);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @androidx.annotation.Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    private Map<Integer, ResultCallback<Intent>> activityResultCallback;

    public void startActivityForResult(@NonNull Intent intent, ResultCallback<Intent> callback) {
        int requestCode = Math.abs((int) System.currentTimeMillis());

        if (activityResultCallback == null) activityResultCallback = new HashMap<>();
        activityResultCallback.put(requestCode, callback);

        this.startActivityForResult(intent, requestCode);
    }

    public void startActivityForResult(@NonNull Intent intent, int requestCode, ResultCallback<Intent> callback) {
        if (activityResultCallback == null) activityResultCallback = new HashMap<>();
        activityResultCallback.put(requestCode, callback);

        this.startActivityForResult(intent, requestCode);
    }

    public void startActivityForResult(@NonNull Intent intent, int requestCode, Bundle options, ResultCallback<Intent> callback) {
        if (activityResultCallback == null) activityResultCallback = new HashMap<>();
        activityResultCallback.put(requestCode, callback);

        this.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (activityResultCallback != null) {
            if (activityResultCallback.containsKey(requestCode)) {
                ResultCallback<Intent> callback = activityResultCallback.remove(requestCode);
                if (callback == null) {
                    Logger.d().print(() -> "startActivityForResult called with NULL callback");
                    return;
                }

                callback.invoke(resultCode == Activity.RESULT_OK ? data : null);
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideWaitView();
        fragmentViewBinding = null;
    }

    //----------------------------------------------------------------------------------------------


    public interface Listener {
        void setKeyboardAutoHidden();

        void showWaitView(int msg); //R.string.wait_moments

        void hideWaitView();

        void hideWaitViewImmediately();

        void updateWaitViewMsg(CharSequence msg);

        RetryPromptDialog showRetryPromptDialog(
                CharSequence msg,
                RetryPromptDialog.Listener onRetry,
                RetryPromptDialog.Listener onCancel
        );

        void showFragment(Fragment fragment, String stackName);

        void showFragment(BaseLegacyFragment fragment, String stackName, int fragmentContainerId);
    }
}