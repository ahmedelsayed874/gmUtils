package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
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

import gmutils.Logger;
import gmutils.R;
import gmutils.ui.dialogs.MessageDialog;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.fragments.BaseFragment;
import gmutils.ui.fragments.BaseFragmentListener;
import gmutils.ui.fragments.BaseFragmentListenerX;
import gmutils.ui.toast.MyToast;
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

        throw new IllegalStateException("You have declare several View Models in getViewModelClasses()");
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
                    ((BaseViewModel) viewModel).progressStatusLiveData().observe(this, getProgressStatusLiveData());
                    ((BaseViewModel) viewModel).alertMessageLiveData().observe(this, getAlertMessageLiveData());
                }
            }
        }
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
        return getActivityFunctions().keyboardShouldAutoHide(rawX, rawY);
    }

    public void keyboardDidHide() {
        getActivityFunctions().keyboardDidHide();
    }

    //----------------------------------------------------------------------------------------------

    public void showWaitView() {
        showWaitView(0);
    }

    public void showWaitView(int msg) {
        showWaitView(thisActivity(), msg);
    }

    @Override
    public void showWaitView(Context context, int msg) {
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
    public void updateWaitViewMsg(CharSequence msg) {
        getActivityFunctions().updateWaitViewMsg(msg);
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
        return showMessageDialog(0, msg);
    }

    public MessageDialog showMessageDialog(int msg, Pair<Integer, MessageDialog.Listener> button) {
        return showMessageDialog(0, msg, button);
    }

    @SafeVarargs
    public final MessageDialog showMessageDialog(int title, int msg, Pair<Integer, MessageDialog.Listener>... buttons) {
        return getActivityFunctions().showMessageDialog(thisActivity(), title, msg, buttons);
    }


    public MessageDialog showMessageDialog(CharSequence msg) {
        return showMessageDialog(null, msg);
    }

    public MessageDialog showMessageDialog(CharSequence msg, Pair<String, MessageDialog.Listener> button) {
        return showMessageDialog(null, msg, button);
    }

    @SafeVarargs
    public final MessageDialog showMessageDialog(CharSequence title, CharSequence msg, Pair<String, MessageDialog.Listener>... buttons) {
        return getActivityFunctions().showMessageDialog(thisActivity(), title, msg, buttons);
    }

    //----------------------------------------------------------------------------------------------

    public void showFragment(Fragment fragment, boolean addToBackStack) {
        showFragment(fragment, addToBackStack, fragment.getClass().getName(), null);
    }

    public void showFragment(Fragment fragment, boolean addToBackStack, String stackName) {
        showFragment(fragment, addToBackStack, stackName, null);
    }

    public void showFragment(Fragment fragment, boolean addToBackStack, Integer fragmentContainerId) {
        showFragment(fragment, addToBackStack, fragment.getClass().getName(), fragmentContainerId);
    }

    @Override
    public void showFragment(Fragment fragment, boolean addToBackStack, String stackName, Integer fragmentContainerId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        fragmentContainerId != null ? fragmentContainerId : R.id.layout_fragment_container,
                        fragment
                );

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(stackName);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

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
                        Logger.print(e);
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

    private Observer<BaseViewModel.ProgressStatus> getProgressStatusLiveData() {
        return progressStatus -> {
            if (progressStatus != null)
                onProgressOfViewModelTaskChanged(progressStatus);
        };
    }

    protected void onProgressOfViewModelTaskChanged(BaseViewModel.ProgressStatus progressStatus) {
        if (progressStatus instanceof BaseViewModel.ProgressStatus.Show) {
            BaseViewModel.ProgressStatus.Show ps = (BaseViewModel.ProgressStatus.Show) progressStatus;
            if (ps.messageId != 0) showWaitView(ps.messageId);
            else showWaitView();

        } else if (progressStatus instanceof BaseViewModel.ProgressStatus.Update) {
            BaseViewModel.ProgressStatus.Update ps = (BaseViewModel.ProgressStatus.Update) progressStatus;
            updateWaitViewMsg(ps.message);

        } else if (progressStatus instanceof BaseViewModel.ProgressStatus.Hide) {
            hideWaitView();
        }
    }

    private Observer<BaseViewModel.Message> getAlertMessageLiveData() {
        return message -> {
            if (message != null) {
                onMessageReceivedFromViewModel(message);
            }
        };
    }

    protected void onMessageReceivedFromViewModel(BaseViewModel.Message message) {
        String msg = "";
        msg = (message.messageId != null) ? getString(message.messageId) : message.messageString;
        if (message.type instanceof BaseViewModel.MessageType.Normal) {
            if (message.popup) {
                showMessageDialog(msg);
            } else {
                MyToast.show(this, msg);
            }
        } else if (message.type instanceof BaseViewModel.MessageType.Error) {
            BaseViewModel.MessageType.Error mt = (BaseViewModel.MessageType.Error) message.type;
            if (message.popup) {
                MessageDialog dialog = showMessageDialog(msg);
                if (mt.button1() != null) {
                    dialog.setButton1(mt.button1().first, d -> mt.button1().second.run());
                }
                if (mt.button2() != null) {
                    dialog.setButton2(mt.button2().first, d -> mt.button2().second.run());
                }
                if (mt.button3() != null) {
                    dialog.setButton3(mt.button3().first, d -> mt.button3().second.run());
                }
            } else {
                MyToast.showError(this, msg);
            }
            mt.destroy();

        } else if (message.type instanceof BaseViewModel.MessageType.Retry) {
            BaseViewModel.MessageType.Retry mt = (BaseViewModel.MessageType.Retry) message.type;
            Runnable onRetry = mt.onRetry();
            mt.destroy();
            showRetryPromptDialog(msg, d -> {
                if (onRetry != null) onRetry.run();
            });
        }
    }

}