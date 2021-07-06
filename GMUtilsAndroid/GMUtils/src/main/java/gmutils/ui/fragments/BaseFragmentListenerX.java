package gmutils.ui.fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public interface BaseFragmentListenerX {

    void showFragment(Fragment fragment, boolean addToBackStack, @Nullable String stackName, @Nullable Integer fragmentContainerId);

    void onFragmentStarted(BaseFragment fragment);
}
