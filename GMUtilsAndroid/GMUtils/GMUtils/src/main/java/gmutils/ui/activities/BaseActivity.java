package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import gmutils.R;
import gmutils.listeners.ResultCallback;
import gmutils.logger.Logger;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.fragments.BaseFragment;
import gmutils.ui.fragments.BaseFragmentListener;
import gmutils.ui.fragments.BaseFragmentListenerX;
import gmutils.ui.fragments.ShowFragmentOptions;
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
@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity implements BaseFragmentListener, BaseFragmentListenerX {

    private ActivityFunctions mActivityFunctions;
    private HashMap<Integer, ViewModel> viewModels;

    //----------------------------------------------------------------------------------------------

    public final ActivityFunctions getActivityFunctions() {
        if (mActivityFunctions == null) {
            mActivityFunctions = new ActivityFunctions(new ActivityFunctions.Delegate() {
                @Override
                public ViewSource getViewSource(@NonNull LayoutInflater inflater) {
                    return BaseActivity.this.getViewSource(inflater);
                }

                @Override
                public CharSequence getActivityTitle() {
                    return BaseActivity.this.getActivityTitle();
                }

                @Override
                public boolean allowApplyingPreferenceLocale() {
                    return BaseActivity.this.allowApplyingPreferenceLocale();
                }

                @Override
                public boolean isOrientationDisabled() {
                    return BaseActivity.this.isOrientationDisabled();
                }

                @Override
                public int initialKeyboardState() {
                    return BaseActivity.this.initialKeyboardState();
                }

                @Override
                public boolean keyboardShouldAutoHide(float rawX, float rawY) {
                    return BaseActivity.this.keyboardShouldAutoHide(rawX, rawY);
                }

                @Override
                public void keyboardDidHide() {
                    BaseActivity.this.keyboardDidHide();
                }
            });
        }
        return mActivityFunctions;
    }

    //----------------------------------------------------------------------------------------------

    @NotNull
    protected abstract ViewSource getViewSource(@NotNull LayoutInflater inflater);

    public ViewBinding getViewBinding() {
        return getActivityFunctions().getViewBinding();
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence getActivityTitle() {
        return "";
    }

    public boolean allowApplyingPreferenceLocale() {
        return true;
    }

    public boolean isOrientationDisabled() {
        return false;
    }

    public int initialKeyboardState() {
        //return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        return WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
    }

    //----------------------------------------------------------------------------------------------

    protected HashMap<Integer, Class<? extends ViewModel>> onPreparingViewModels() {
        return null;
    }

    protected ViewModelProvider.Factory onCreateViewModelFactory(int viewModelId) {
        return ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getApplication());
    }

    public ViewModel getViewModel() {
        if (viewModels.size() == 1) {
            return viewModels.values().toArray(new ViewModel[0])[0];
        }

        if (viewModels.size() == 0) {
            throw new IllegalStateException("You didn't declare any View Models in getViewModelClasses()");
        } else {
            throw new IllegalStateException("You have declared several View Models in getViewModelClasses()");
        }
    }

    public ViewModel getViewModel(int id) {
        return viewModels.get(id);
    }

    //----------------------------------------------------------------------------------------------

    protected void onPreCreate() {
        getActivityFunctions().lifecycle().onPreCreate(thisActivity());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getActivityFunctions();//to create the object

        onPreCreate();
        super.onCreate(savedInstanceState);
        getActivityFunctions().lifecycle().onCreate(thisActivity(), savedInstanceState);

        HashMap<Integer, Class<? extends ViewModel>> viewModelClasses = onPreparingViewModels();
        if (viewModelClasses != null) {
            viewModels = new HashMap<>();
            for (Integer id : viewModelClasses.keySet()) {
                ViewModelProvider.Factory viewModelFactory = onCreateViewModelFactory(id);

                ViewModelProvider viewModelProvider = new ViewModelProvider(
                        thisActivity(),
                        viewModelFactory
                );

                Class<? extends ViewModel> viewModelClass = viewModelClasses.get(id);
                assert viewModelClass != null;
                ViewModel viewModel = viewModelProvider.get(viewModelClass);
                viewModels.put(id, viewModel);

                if (viewModel instanceof BaseViewModel) {
                    ((BaseViewModel) viewModel).progressStatusLiveData().observe(this, getProgressStatusLiveDataObserver());
                    ((BaseViewModel) viewModel).alertMessageLiveData().observe(this, getAlertMessageLiveDataObserver());
                    ((BaseViewModel) viewModel).updateUiLiveData().observe(this, getUpdateUiLiveDataObserver());
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getActivityFunctions().lifecycle().onNewIntent(thisActivity(), intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivityFunctions().lifecycle().onStart(thisActivity());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getActivityFunctions().lifecycle().onConfigurationChanged(thisActivity(), newConfig);
    }

    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(getActivityFunctions().lifecycle().getAttachBaseContext(newBase));
    }

    //----------------------------------------------------------------------------------------------

    public Activity thisActivity0() {
        return thisActivity();
    }

    public AppCompatActivity thisActivity() {
        return this;
    }

    @Override
    public void setKeyboardAutoHidden(Activity activity) {
        getActivityFunctions().setKeyboardAutoHidden(activity);
    }

    public void setKeyboardAutoHidden() {
        setKeyboardAutoHidden(thisActivity());
    }

    public boolean keyboardShouldAutoHide(float rawX, float rawY) {
        return true;
    }

    public void keyboardDidHide() {
    }

    //----------------------------------------------------------------------------------------------

    public void showWaitView() {
        showWaitView(0);
    }

    public void showWaitView(int msg) {
        showWaitView(thisActivity(), msg);
    }

    public void showWaitView(CharSequence msg) {
        showWaitView(thisActivity(), msg);
    }

    @Override
    public void showWaitView(Context context, int msg) {
        getActivityFunctions().showWaitView(context, msg);
    }

    @Override
    public void showWaitView(Context context, CharSequence msg) {
        getActivityFunctions().showWaitView(context, msg);
    }

    @Override
    public void hideWaitView() {
        getActivityFunctions().hideWaitView();
    }

    @Override
    public void hideWaitViewImmediately() {
        getActivityFunctions().hideWaitViewImmediately();
    }

    @Override
    public boolean updateWaitViewMsg(CharSequence msg) {
        boolean b = getActivityFunctions().updateWaitViewMsg(msg);
        if (!b) {
            getActivityFunctions().hideWaitViewImmediately();
            getActivityFunctions().showWaitView(this, msg);
            return true;
        }
        return b;
    }

    public void updateWaitViewMsg(int msg) {
        getActivityFunctions().updateWaitViewMsg(getString(msg));
    }

    public boolean isWaitViewShown() {
        return getActivityFunctions().isWaitViewShown();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public RetryPromptDialog showRetryPromptDialog(Context context, CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return getActivityFunctions().showRetryPromptDialog(context, msg, onRetry, onCancel);
    }

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry) {
        return showRetryPromptDialog(thisActivity(), msg, onRetry, null);
    }

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return showRetryPromptDialog(thisActivity(), msg, onRetry, onCancel);
    }

    //----------------------------------------------------------------------------------------------

    public MessageDialog showMessageDialog(int msg) {
        return showMessageDialog(getString(msg));
    }

    public MessageDialog showMessageDialog(CharSequence msg) {
        return getActivityFunctions().showMessageDialog(this, msg);
    }

    @Override
    public MessageDialog showMessageDialog(Context context, CharSequence msg) {
        return getActivityFunctions().showMessageDialog(context, msg);
    }

    //----------------------------------------------------------------------------------------------

    public void showFragment(Fragment fragment, boolean addToBackStack) {
        showFragment(
                fragment,
                new ShowFragmentOptions()
                        .setAddToBackStack(addToBackStack)
                        .setStackName(fragment.getClass().getName())
        );
    }

    public void showFragment(Fragment fragment, boolean addToBackStack, String stackName) {
        showFragment(
                fragment,
                new ShowFragmentOptions()
                        .setAddToBackStack(addToBackStack)
                        .setStackName(stackName)
        );
    }

    public void showFragment(Fragment fragment, boolean addToBackStack, Integer fragmentContainerId) {
        showFragment(
                fragment,
                new ShowFragmentOptions()
                        .setAddToBackStack(addToBackStack)
                        .setStackName(fragment.getClass().getName())
                        .setFragmentContainerId(fragmentContainerId)
        );
    }

    @SuppressLint("WrongConstant")
    @Override
    public void showFragment(Fragment fragment, ShowFragmentOptions options) {
        int fragmentContainerId = R.id.layout_fragment_container;
        if (options != null && options.getFragmentContainerId() != null) {
            fragmentContainerId = options.getFragmentContainerId();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainerId, fragment);

        if (options != null) {
            if (options.isAddToBackStack()) {
                fragmentTransaction.addToBackStack(options.getStackName());
            }
        }

        if (options != null) {
            if (options.getTransition() != null) {
                try {
                    fragmentTransaction.setTransition(options.getTransition());
                } catch (Exception ignore) {
                }
            }
        }

        fragmentTransaction.commit();
    }

    public void showFragmentOnTop(Fragment fragment) {
        showFragmentOnTop(fragment, fragment.getClass().getName());
    }

    public void showFragmentOnTop(Fragment fragment, String stackName) {
        Fragment currentFragment = getCurrentDisplayedFragment();

        if (currentFragment != null) {
            if (currentFragment.getClass() != fragment.getClass()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                boolean pop = fragmentManager.popBackStackImmediate(stackName, 0);

                if (!pop) {
                    showFragment(fragment, true, stackName);

                } else {
                    try {
                        currentFragment = fragmentManager.getFragments().get(0);
                        currentFragment.setArguments(fragment.getArguments());
                    } catch (Exception e) {
                        Logger.d().print(e);
                    }
                }
            } else {
                currentFragment.setArguments(fragment.getArguments());
            }
        } else {
            showFragment(fragment, true, stackName);
        }
    }

    public Fragment getCurrentDisplayedFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 0)
            return fragments.get(fragments.size() - 1);
        else
            return null;
    }

    @Override
    public void onFragmentStarted(BaseFragment fragment) {

    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getActivityFunctions().lifecycle().onDestroy();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivityFunctions().lifecycle().onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivityFunctions().lifecycle().onOptionsItemSelected(thisActivity(), item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    //----------------------------------------------------------------------------------------------

    private Observer<BaseViewModel.ProgressStatus> getProgressStatusLiveDataObserver() {
        return progressStatus -> {
            if (progressStatus != null)
                onProgressOfViewModelTaskChanged(progressStatus);
        };
    }

    protected void onProgressOfViewModelTaskChanged(BaseViewModel.ProgressStatus progressStatus) {
        new BaseViewModelObserversHandlers().onProgressOfViewModelTaskChanged(
                this,
                progressStatus,

                //showWaitView
                this::showWaitView,

                //updateWaitViewMsg
                this::updateWaitViewMsg,

                //hideWaitView
                (forceHide) -> {
                    if (forceHide)
                        hideWaitViewImmediately();
                    else
                        hideWaitView();
                }
        );
    }

    private Observer<BaseViewModel.Message> getAlertMessageLiveDataObserver() {
        return message -> {
            if (message != null) {
                onMessageReceivedFromViewModel(message);
            }
        };
    }
    protected void onMessageReceivedFromViewModel(BaseViewModel.Message message) {
        new BaseViewModelObserversHandlers().onMessageReceivedFromViewModel(
                this,
                message,

                //showMessageDialog,
                m -> showMessageDialog(m),

                //showToast
                (m, normal) -> {
                    if (normal) MyToast.show(this, m);
                    else MyToast.showError(this, m);
                },

                //showRetryPromptDialog
                (m, a) -> showRetryPromptDialog(m, d -> a.run())
        );
    }

    private Observer<String> getUpdateUiLiveDataObserver() {
        return this::onViewModelUpdatesUi;
    }

    protected void onViewModelUpdatesUi(String args) {
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode, @androidx.annotation.Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    public void startActivityForResult(@NonNull Intent intent, ResultCallback<Intent> callback) {
        getActivityFunctions().startActivityForResult(
                this,
                intent,
                (int) System.currentTimeMillis(),
                callback,
                null
        );
    }

    public void startActivityForResult(@NonNull Intent intent, int requestCode, ResultCallback<Intent> callback) {
        getActivityFunctions().startActivityForResult(
                this,
                intent,
                requestCode,
                callback,
                null
        );
    }

    public void startActivityForResult(@NonNull Intent intent, int requestCode, ResultCallback<Intent> callback, Bundle options) {
        getActivityFunctions().startActivityForResult(
                this,
                intent,
                requestCode,
                callback,
                options
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivityFunctions().onActivityResult(requestCode, resultCode, data);
    }
}