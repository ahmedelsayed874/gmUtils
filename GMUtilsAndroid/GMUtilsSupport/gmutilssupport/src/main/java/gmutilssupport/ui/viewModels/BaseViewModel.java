package gmutilssupport.ui.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import gmutils.collections.MultiLanguageString;
import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;


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

    public interface ProgressStatus {
        int getProgress();
        ProgressStatus setProgress(int progress);

        class Show implements ProgressStatus {
            public final int messageId;
            private int progress = 0;

            public Show(int messageId) {
                this.messageId = messageId;
            }

            @Override
            public int getProgress() {
                return progress;
            }

            @Override
            public ProgressStatus setProgress(int progress) {
                this.progress = progress;
                return this;
            }
        }

        class Update implements ProgressStatus {
            public final String message;
            private int progress = 0;

            public Update(String message) {
                this.message = message;
            }

            @Override
            public int getProgress() {
                return progress;
            }

            @Override
            public ProgressStatus setProgress(int progress) {
                this.progress = progress;
                return this;
            }
        }

        class Hide implements ProgressStatus {
            private int progress = 0;

            @Override
            public int getProgress() {
                return progress;
            }

            @Override
            public ProgressStatus setProgress(int progress) {
                this.progress = progress;
                return this;
            }
        }
    }

    public static class Message {
        public final List<Integer> messageIds;
        public final MultiLanguageString messageString;
        public final boolean popup;
        public final MessageType type;
        private String multiMessageIdsPrefix = "-";
        private String multiMessageIdsSeparator = "\n";

        public Message(Integer messageId) {
            this(messageId, null, false, new MessageType.Normal());
        }

        public Message(String messageString) {
            this(null, messageString, false, new MessageType.Normal());
        }

        public Message(Integer messageId, boolean popup, MessageType type) {
            this(messageId, null, popup, type);
        }

        public Message(String messageString, boolean popup, MessageType type) {
            this(null, messageString, popup, type);
        }

        private Message(Integer messageId, String messageString, boolean popup, MessageType type) {
            if (messageId != null) {
                this.messageIds = new ArrayList<>();
                this.messageIds.add(messageId);
            } else {
                this.messageIds = null;
            }

            if (messageString != null) {
                this.messageString = new MultiLanguageString(messageString);
            } else {
                this.messageString = null;
            }

            this.popup = popup;
            this.type = type;
        }

        public Message(@NotNull List<Integer> messageIds, boolean popup, MessageType type) {
            this.messageIds = messageIds;
            this.messageString = null;
            this.popup = popup;
            this.type = type;
        }

        public Message(@NotNull MultiLanguageString messageString, boolean popup, MessageType type) {
            this.messageIds = null;
            this.messageString = messageString;
            this.popup = popup;
            this.type = type;
        }

        public Message setMultiMessageIdsPrefix(String multiMessageIdsPrefix) {
            this.multiMessageIdsPrefix = multiMessageIdsPrefix;
            return this;
        }

        public String getMultiMessageIdsPrefix() {
            return multiMessageIdsPrefix;
        }

        public Message setMultiMessageIdsSeparator(String multiMessageIdsSeparator) {
            this.multiMessageIdsSeparator = multiMessageIdsSeparator;
            return this;
        }

        public String getMultiMessageIdsSeparator() {
            return multiMessageIdsSeparator;
        }
    }

    public interface MessageType {
        void destroy();

        class Normal implements MessageType {
            @Override
            public void destroy() {
            }
        }

        class Error implements MessageType {
            private Pair<Integer, Runnable> button1;
            private Pair<Integer, Runnable> button2;
            private Pair<Integer, Runnable> button3;

            public Error() {
                this(null, null, null);
            }

            public Error(Pair<Integer, Runnable> button1) {
                this(button1, null, null);
            }

            public Error(Pair<Integer, Runnable> button1, Pair<Integer, Runnable> button2) {
                this(button1, button2, null);
            }

            public Error(Pair<Integer, Runnable> button1, Pair<Integer, Runnable> button2, Pair<Integer, Runnable> button3) {
                this.button1 = button1;
                this.button2 = button2;
                this.button3 = button3;
            }

            public boolean hasSpecialButtons() {
                return button1 != null || button2 != null || button3 != null;
            }

            public final Pair<Integer, Runnable> button1() {
                return button1;
            }

            public final Pair<Integer, Runnable> button2() {
                return button2;
            }

            public final Pair<Integer, Runnable> button3() {
                return button3;
            }

            @Override
            public void destroy() {
                button1 = null;
                button2 = null;
                button3 = null;
            }
        }

        class Retry implements MessageType {
            private Runnable _onRetry;

            public Retry(Runnable onRetry) {
                this._onRetry = onRetry;
            }

            public final Runnable onRetry() {
                return _onRetry;
            }

            public void destroy() {
                this._onRetry = null;
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private Handler handler = null;
    private final MutableLiveData<ProgressStatus> progressStatusLiveData;
    private final MutableLiveData<Message> alertMessageLiveData;

    public BaseViewModel(@NotNull Application application) {
        super(application);

        progressStatusLiveData = new MutableLiveData<>();
        alertMessageLiveData = new MutableLiveData<>();

    }

    //----------------------------------------------------------------------------------------------

    public MutableLiveData<ProgressStatus> progressStatusLiveData() {
        return progressStatusLiveData;
    }

    public MutableLiveData<Message> alertMessageLiveData() {
        return alertMessageLiveData;
    }

    //----------------------------------------------------------------------------------------------

    public void postProgressStatusOnUIThread(ProgressStatus progressStatus) {
        runOnUIThread(() -> postProgressStatus(progressStatus));
    }

    public void postProgressStatus(ProgressStatus progressStatus) {
        progressStatusLiveData.postValue(progressStatus);
    }

    public void postMessage(Message message) {
        alertMessageLiveData.postValue(message);
    }

    public void postPopupMessage(int messageId) {
        Message m = new Message(messageId, true, new MessageType.Normal());
        postMessage(m);
    }

    public void postPopupMessage(String message) {
        Message m = new Message(message, true, new MessageType.Normal());
        postMessage(m);
    }

    public void postPopupMessage(int messageId, MessageType messageType) {
        Message m = new Message(messageId, true, messageType);
        postMessage(m);
    }

    public void postPopupMessage(String message, MessageType messageType) {
        Message m = new Message(message, true, messageType);
        postMessage(m);
    }

    public void postToastMessage(int messageId, boolean error) {
        Message m = new Message(messageId, false, error ? new MessageType.Error() : new MessageType.Normal());
        postMessage(m);
    }

    public void postToastMessage(String message, boolean error) {
        Message m = new Message(message, false, error ? new MessageType.Error() : new MessageType.Normal());
        postMessage(m);
    }

    public void postRetryMessage(String message, Runnable onRetry) {
        Message m = new Message(message, true, new MessageType.Retry(onRetry));
        postMessage(m);
    }

    //----------------------------------------------------------------------------------------------

    public void runOnUIThread(Runnable runnable) {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void runOnUIThread(Runnable runnable, long delay) {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, boolean dispatchResultOnUIThread, ResultCallback<T> onFinish) {
        runOnBackgroundThread(null, task, dispatchResultOnUIThread, onFinish);
    }

    public <T> void runOnBackgroundThread(String name, ActionCallback0<T> task, boolean dispatchResultOnUIThread, ResultCallback<T> onFinish) {
        if (task == null) return;

        Runnable target = () -> {
            T result = task.invoke();
            if (onFinish != null) {
                if (dispatchResultOnUIThread)
                    runOnUIThread(() -> onFinish.invoke(result));
                else
                    onFinish.invoke(result);
            }
        };

        if (TextUtils.isEmpty(name))
            new Thread(target).start();
        else
            new Thread(target, name).start();
    }
    
    public void runOnBackgroundThread(Runnable task) {
        if (task == null) return;
        new Thread(task).start();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}