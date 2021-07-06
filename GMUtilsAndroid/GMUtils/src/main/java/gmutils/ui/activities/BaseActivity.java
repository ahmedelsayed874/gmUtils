package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import gmutils.Logger;
import gmutils.R;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.fragments.BaseFragment;
import gmutils.ui.fragments.BaseFragmentListenerX;
import gmutils.ui.utils.ViewSource;

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
@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity implements BaseFragmentListenerX {

    private ActivityFunctions mActivityFunctions;
    private HashMap<Integer, ViewModel> viewModels;

    //----------------------------------------------------------------------------------------------

    public final ActivityFunctions getActivityFunctions() {
        return mActivityFunctions;
    }

    //----------------------------------------------------------------------------------------------

    private ViewBinding activityViewBinding;

    @NotNull
    protected abstract ViewSource getViewSource(@NotNull LayoutInflater inflater);

    public final ViewBinding getActivityViewBinding() {
        return activityViewBinding;
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

    protected ViewModelProvider.Factory onCreateViewModelFactory(int id) {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getApplication());
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

    protected void onPreCreate() {
        mActivityFunctions.onPreCreate(thisActivity());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mActivityFunctions = new ActivityFunctions(new ActivityFunctions.Delegate() {
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

        onPreCreate();

        super.onCreate(savedInstanceState);

        mActivityFunctions.onCreate(thisActivity(), savedInstanceState);

        ViewSource viewSource = getViewSource(getLayoutInflater());

        if (viewSource instanceof ViewSource.LayoutResource) {
            setContentView(((ViewSource.LayoutResource) viewSource).getResourceId());

        } else if (viewSource instanceof ViewSource.View) {
            setContentView(((ViewSource.View) viewSource).getView());

        } else if (viewSource instanceof ViewSource.ViewBinding) {
            activityViewBinding = ((ViewSource.ViewBinding) viewSource).getViewBinding();
            setContentView(activityViewBinding.getRoot());
        }

        onPostCreate();

    }

    protected void onPostCreate() {
        mActivityFunctions.onPostCreate(thisActivity());

        HashMap<Integer, Class<? extends ViewModel>> viewModelClasses = onPreparingViewModels();
        if (viewModelClasses != null) {
            viewModels = new HashMap<>();
            for (Integer id : viewModelClasses.keySet()) {
                ViewModelProvider viewModelProvider = new ViewModelProvider(
                        thisActivity(),
                        onCreateViewModelFactory(id)
                );

                Class<? extends ViewModel> viewModelClass = viewModelClasses.get(id);
                assert viewModelClass != null;
                viewModels.put(id, viewModelProvider.get(viewModelClass));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivityFunctions.onStart(thisActivity());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActivityFunctions.onConfigurationChanged(thisActivity(), newConfig);
    }

    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(mActivityFunctions.getAttachBaseContext(newBase));
    }

    //----------------------------------------------------------------------------------------------

    public Activity thisActivity0() {
        return thisActivity();
    }

    public AppCompatActivity thisActivity() {
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setKeyboardAutoHidden() {
        mActivityFunctions.setKeyboardAutoHidden(thisActivity());
    }

    public boolean keyboardShouldAutoHide(float rawX, float rawY) {
        return mActivityFunctions.keyboardShouldAutoHide(rawX, rawY);
    }

    public void keyboardDidHide() {
        mActivityFunctions.keyboardDidHide();
    }

    //----------------------------------------------------------------------------------------------

    public void showWaitView() {
        showWaitView(0);
    }

    public void showWaitView(int msg) {
        mActivityFunctions.showWaitView(thisActivity(), msg);
    }

    public void hideWaitView() {
        mActivityFunctions.hideWaitView();
    }

    public void hideWaitViewImmediately() {
        mActivityFunctions.hideWaitViewImmediately();
    }

    public void updateWaitViewMsg(CharSequence msg) {
        mActivityFunctions.updateWaitViewMsg(msg);
    }

    public boolean isWaitViewShown() {
        return mActivityFunctions.isWaitViewShown();
    }

    //----------------------------------------------------------------------------------------------

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry) {
        return mActivityFunctions.showRetryPromptDialog(thisActivity(), msg, onRetry, null);
    }

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return mActivityFunctions.showRetryPromptDialog(thisActivity(), msg, onRetry, onCancel);
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
        mActivityFunctions.onDestroy();
        if (viewModels != null) viewModels.clear();
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mActivityFunctions.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActivityFunctions.onOptionsItemSelected(thisActivity(), item)) return true;
        return super.onOptionsItemSelected(item);
    }
}