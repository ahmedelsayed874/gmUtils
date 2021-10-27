package gmutilssupport.ui.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public interface BaseFragmentListenerX {

    void showFragment(Fragment fragment, boolean addToBackStack, @Nullable String stackName, @Nullable Integer fragmentContainerId);

    void onFragmentStarted(BaseFragment fragment);
}
