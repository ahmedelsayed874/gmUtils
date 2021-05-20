package gmutils.ui.viewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;


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
public class BaseViewModel extends AndroidViewModel {

    private Handler handler = null;

    public BaseViewModel(@NotNull Application application) {
        super(application);

    }

    public void runOnUIThread(Runnable runnable) {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void runOnUIThread(Runnable runnable, long delay) {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}