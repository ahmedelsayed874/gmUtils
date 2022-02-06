package gmutils.ui.fragments;

import androidx.fragment.app.Fragment;

public interface BaseFragmentListenerX {

    void showFragment(Fragment fragment, ShowFragmentOptions options);

    void onFragmentStarted(BaseFragment fragment);
}
