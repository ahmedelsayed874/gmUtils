package gmutilssupport.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import gmutils.Logger;
import gmutilsSupport.R;
import gmutilssupport.ui.dialogs.MessageDialog;
import gmutilssupport.ui.dialogs.RetryPromptDialog;
import gmutilssupport.ui.fragments.BaseLegacyFragment;
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
@SuppressLint("Registered")
public abstract class BaseLegacyActivity extends Activity implements BaseLegacyFragment.Listener {

    private ActivityFunctions _activityFunctions;

    //----------------------------------------------------------------------------------------------

    public final ActivityFunctions getActivityFunctions() {
        if (_activityFunctions == null) {
            _activityFunctions = new ActivityFunctions(new ActivityFunctions.Delegate() {
                @Override
                public ViewSource getViewSource(LayoutInflater inflater) {
                    return BaseLegacyActivity.this.getViewSource(inflater);
                }

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
        }
        return _activityFunctions;
    }

    //----------------------------------------------------------------------------------------------

    @NotNull
    protected abstract ViewSource getViewSource(@NotNull LayoutInflater inflater);

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
        getActivityFunctions().lifecycle().onPreCreate(thisActivity());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onPreCreate();

        super.onCreate(savedInstanceState);

        getActivityFunctions().lifecycle().onCreate(thisActivity(), savedInstanceState);

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

    public Activity thisActivity() {
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setKeyboardAutoHidden() {
        getActivityFunctions().setKeyboardAutoHidden(thisActivity());
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
        getActivityFunctions().showWaitView(thisActivity(), msg);
    }

    public void hideWaitView() {
        getActivityFunctions().hideWaitView();
    }

    public void hideWaitViewImmediately() {
        getActivityFunctions().hideWaitViewImmediately();
    }

    public void updateWaitViewMsg(CharSequence msg) {
        getActivityFunctions().updateWaitViewMsg(msg);
    }

    public boolean isWaitViewShown() {
        return getActivityFunctions().isWaitViewShown();
    }

    //----------------------------------------------------------------------------------------------

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry) {
        return getActivityFunctions().showRetryPromptDialog(thisActivity(), msg, onRetry, null);
    }

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return getActivityFunctions().showRetryPromptDialog(thisActivity(), msg, onRetry, onCancel);
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
        getActivityFunctions().destroy();
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivityFunctions().lifecycle().onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivityFunctions().lifecycle().onOptionsItemSelected(thisActivity(), item)) return true;
        return super.onOptionsItemSelected(item);
    }
}