package com.blogspot.gm4s1.gmutils.ui._bases;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.gm4s1.gmutils.Logger;
import com.blogspot.gm4s1.gmutils.KeypadOp;
import com.blogspot.gm4s1.gmutils.ui.MyToast;
import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils.utils.Utils;
import com.blogspot.gm4s1.gmutils.ui.dialogs.MessageDialog;
import com.blogspot.gm4s1.gmutils.ui.dialogs.RetryPromptDialog;
import com.blogspot.gm4s1.gmutils.ui.dialogs.WaitDialog;
import com.blogspot.gm4s1.gmutils.storage.SettingsStorage;

import java.util.HashMap;
import java.util.List;

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
public abstract class BaseActivity extends AppCompatActivity implements BaseFragment.Listener {

    private WaitDialog waitDialog = null;
    private int waitDialogCount = 0;
    private HashMap<Integer, BaseViewModel> viewModels;


    public abstract int getActivityLayout();

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

    protected HashMap<Integer, Class<? extends BaseViewModel>> getViewModelClasses() {
        return null;
    }

    protected ViewModelProvider.Factory getViewModelFactory(int id) {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getApplication());
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

    protected void onPreCreate() {
        if (allowApplyingPreferenceLocale()) {
            SettingsStorage.getInstance().languagePref().applySavedLanguage(thisActivity());
        }

        if (isOrientationDisabled())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            getWindow().setSoftInputMode(initialKeyboardState());
        } catch (Exception e) {
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        onPreCreate();

        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());

        onPostCreate();

    }

    protected void onPostCreate() {
        HashMap<Integer, Class<? extends BaseViewModel>> viewModelClasses = getViewModelClasses();
        if (viewModelClasses != null) {
            viewModels = new HashMap<>();

            for (Integer id : viewModelClasses.keySet()) {
                ViewModelProvider viewModelProvider = new ViewModelProvider(
                        thisActivity(),
                        getViewModelFactory(id)
                );

                Class<? extends BaseViewModel> viewModelClass = viewModelClasses.get(id);
                assert viewModelClass != null;
                viewModels.put(id, viewModelProvider.get(viewModelClass));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(getActivityTitle())) setTitle(getActivityTitle());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SettingsStorage.getInstance().languagePref().applySavedLanguage(thisActivity());
    }

    public void attachBaseContext(Context newBase) {
        if (allowApplyingPreferenceLocale()) {
            super.attachBaseContext(
                    SettingsStorage.getInstance().languagePref().createNewContext(newBase)
            );
        } else {
            super.attachBaseContext(newBase);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public Activity thisActivity0() {
        return thisActivity();
    }

    public AppCompatActivity thisActivity() {
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setKeyboardAutoHidden() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        View topView = new View(thisActivity());
        topView.setLayoutParams(layoutParams);
        topView.setOnTouchListener((v, e) -> {
            if (keyboardShouldAutoHide(e.getRawX(), e.getRawY())) {
                KeypadOp.hide(thisActivity());
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) currentFocus.clearFocus();
                keyboardDidHide();
            }
            return false;
        });

        ViewGroup contentView = findViewById(android.R.id.content);
        contentView.addView(topView);
    }

    public boolean keyboardShouldAutoHide(float rawX, float rawY) {
        return true;
    }

    public void keyboardDidHide() {
    }

    //------------------------------------------------------------------------------------------------------------------

    public void showWaitView() {
        showWaitView(0);
    }

    @Override
    public void showWaitView(int msg) {
        if (waitDialogCount == 0) onPreparingWaitView(msg == 0 ? R.string.wait_moments : msg);

        waitDialogCount++;
    }

    @Override
    public void hideWaitView() {
        if (waitDialogCount == 1) {
            onHidingWaitView();
            waitDialog = null;
        }

        waitDialogCount--;
        if (waitDialogCount < 0) waitDialogCount = 0;
    }

    @Override
    public void updateWaitViewMsg(CharSequence msg) {
        if (waitDialog != null) waitDialog.textView().setText(msg);
    }

    public void onPreparingWaitView(int msg) {
        if (msg == 0) msg = R.string.wait_moments;
        waitDialog = WaitDialog.show(thisActivity(), msg);
    }

    public void onHidingWaitView() {
        if (waitDialog != null) waitDialog.dismiss();
    }

    //------------------------------------------------------------------------------------------------------------------

    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry) {
        return RetryPromptDialog.show(thisActivity(), msg, onRetry, null);
    }

    @Override
    public RetryPromptDialog showRetryPromptDialog(CharSequence msg, RetryPromptDialog.Listener onRetry, RetryPromptDialog.Listener onCancel) {
        return RetryPromptDialog.show(thisActivity(), msg, onRetry, onCancel);
    }

    //------------------------------------------------------------------------------------------------------------------


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

    //------------------------------------------------------------------------------------------------------------------


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (waitDialog != null) waitDialog.dismiss();
        waitDialogCount = 0;
        waitDialog = null;

        if (viewModels != null) viewModels.clear();
    }


    //------------------------------------------------------------------------------------------------------------------

    private int showBugsMenuItemId = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Logger.IS_WRITE_TO_FILE_ENABLED()) {
            if (showBugsMenuItemId == 0) {
                showBugsMenuItemId = "Show Bug Log".hashCode();
                if (showBugsMenuItemId < 0) showBugsMenuItemId *= -1;
            }
            menu.add(0, showBugsMenuItemId, 10, "Show Bug Log");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {
                thisActivity().onBackPressed();
            } catch (Exception e) {
            }
        }

        if (item.getItemId() == showBugsMenuItemId) {
            String txt = Logger.readAllFilesContents(thisActivity());

            MessageDialog.create(thisActivity())
                    .setMessage(txt)
                    .setMessageGravity(Gravity.START)
                    .setButton1(R.string.copy, (d) -> {
                        if (Utils.createInstance().copyText(thisActivity(), txt)) {
                            MyToast.showError(thisActivity(), "copied");
                        } else {
                            MyToast.showError(thisActivity(), "failed");
                        }
                    })
                    .setButton2(R.string.delete, (d) -> {
                        Logger.deleteSavedFiles(thisActivity());
                    })
                    .show();

        }

        return super.onOptionsItemSelected(item);
    }
}