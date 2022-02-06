package gmutilssupport.ui.fragments;

import android.support.v4.app.Fragment;

public interface BaseFragmentListenerX {

    void showFragment(Fragment fragment, ShowFragmentOptions options);

    void onFragmentStarted(BaseFragment fragment);
}
