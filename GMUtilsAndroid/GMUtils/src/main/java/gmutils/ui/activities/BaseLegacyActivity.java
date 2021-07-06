package gmutils.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import gmutils.Logger;
import gmutils.R;
import gmutils.ui.dialogs.RetryPromptDialog;
import gmutils.ui.fragments.BaseLegacyFragment;
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
public abstract class BaseLegacyActivity extends Activity implements BaseLegacyFragment.Listener {

    private ActivityFunctions mActivityFunctions;

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

    protected void onPreCreate() {
        mActivityFunctions.onPreCreate(thisActivity());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mActivityFunctions = new ActivityFunctions(new ActivityFunctions.Delegate() {
            @Override
            public CharSequence getActivityTitle() {
                return BaseLegacyActivity.this.getActivityTitle();
            }

            @Override
            public boolean allowApplyingPreferenceLocale() {
                return BaseLegacyActivity.this.allowApplyingPreferenceLocale();
            }

            @Override
            public boolean isOrientationDisabled() {
                return BaseLegacyActivity.this.isOrientationDisabled();
            }

            @Override
            public int initialKeyboardState() {
                return BaseLegacyActivity.this.initialKeyboardState();
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

    public Activity thisActivity() {
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
        showFragment(fragment, addToBackStack, null, R.id.layout_fragment_container);
    }

    @Override
    public void showFragment(Fragment fragment, String stackName) {
        showFragment(fragment, true, stackName, R.id.layout_fragment_container);
    }

    @Override
    public void showFragment(BaseLegacyFragment fragment, String stackName, int fragmentContainerId) {
        showFragment(fragment, true, stackName, fragmentContainerId);
    }

    public void showFragment(Fragment fragment, Boolean addToBackStack, String stackName, int fragmentContainerId) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction()
                .replace(fragmentContainerId, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(stackName);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFragmentOnTop(Fragment fragment) {
        showFragmentOnTop(fragment, fragment.getClass().getName());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFragmentOnTop(Fragment fragment, String stackName) {
        Fragment currentFragment = getCurrentDisplayedFragment();

        if (currentFragment != null) {
            if (currentFragment.getClass() != fragment.getClass()) {
                FragmentManager fragmentManager = getFragmentManager();
                boolean pop = fragmentManager.popBackStackImmediate(stackName, 0);

                if (!pop) {
                    showFragment(fragment, stackName);

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
            showFragment(fragment, stackName);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Fragment getCurrentDisplayedFragment() {
        List<Fragment> fragments = getFragmentManager().getFragments();
        if (fragments.size() > 0)
            return fragments.get(fragments.size() - 1);
        else
            return null;
    }

    //----------------------------------------------------------------------------------------------


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityFunctions.onDestroy();
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