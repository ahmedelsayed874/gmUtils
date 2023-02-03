package gmutils.ui.viewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import gmutils.StringSet;
import gmutils.collections.dataGroup.DataGroup3;
import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;


/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class BaseViewModel extends AndroidViewModel {

    public interface ProgressStatus {

        class Show implements ProgressStatus {
            public final String message;
            public final int messageId;

            public Show() {
                this.message = null;
                this.messageId = 0;
            }

            public Show(int messageId) {
                this.message = null;
                this.messageId = messageId;
            }

            public Show(String message) {
                this.message = message;
                this.messageId = 0;
            }

        }

        class Update implements ProgressStatus {
            public final String message;
            public final int messageId;

            public Update(int messageId) {
                this.message = null;
                this.messageId = messageId;
            }

            public Update(String message) {
                this.message = message;
                this.messageId = 0;
            }

        }

        class Hide implements ProgressStatus {
        }
    }

    public static class Message {
        public final List<Integer> messageIds;
        public final StringSet messageString;
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
                this.messageString = new StringSet(messageString);
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

        public Message(@NotNull StringSet messageString, boolean popup, MessageType type) {
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
            private DataGroup3<Integer, String, Runnable> button1;
            private DataGroup3<Integer, String, Runnable> button2;
            private DataGroup3<Integer, String, Runnable> button3;

            public Error() {
                this(
                        (String) null, null,
                        (String) null, null,
                        (String) null, null
                );
            }

            public Error(Integer button1Text, Runnable button1Action) {
                this(
                        button1Text, button1Action,
                        (Integer) null, null,
                        (Integer) null, null
                );
            }

            public Error(String button1Text, Runnable button1Action) {
                this(
                        button1Text, button1Action,
                        (String) null, null,
                        (String) null, null
                );
            }


            public Error(Integer button1Text, Runnable button1Action, Integer button2Text, Runnable button2Action) {
                this(
                        button1Text, button1Action,
                        button2Text, button2Action,
                        (Integer) null, null
                );
            }

            public Error(String button1Text, Runnable button1Action, String button2Text, Runnable button2Action) {
                this(
                        button1Text, button1Action,
                        button2Text, button2Action,
                        (String) null, null
                );
            }


            public Error(Integer button1Text, Runnable button1Action, Integer button2Text, Runnable button2Action, Integer button3Text, Runnable button3Action) {
                this.button1 = (button1Text == null || button1Text == 0) && button1Action == null ? null : new DataGroup3<>(button1Text, null, button1Action);
                this.button2 = (button2Text == null || button2Text == 0) && button2Action == null ? null : new DataGroup3<>(button2Text, null, button2Action);
                this.button3 = (button3Text == null || button3Text == 0) && button3Action == null ? null : new DataGroup3<>(button3Text, null, button3Action);
            }

            public Error(String button1Text, Runnable button1Action, String button2Text, Runnable button2Action, String button3Text, Runnable button3Action) {
                this.button1 = TextUtils.isEmpty(button1Text) && button1Action == null ? null : new DataGroup3<>(null, button1Text, button1Action);
                this.button2 = TextUtils.isEmpty(button2Text) && button2Action == null ? null : new DataGroup3<>(null, button2Text, button2Action);
                this.button3 = TextUtils.isEmpty(button3Text) && button3Action == null ? null : new DataGroup3<>(null, button3Text, button3Action);
            }


            public boolean hasSpecialButtons() {
                return button1 != null || button2 != null || button3 != null;
            }

            public final DataGroup3<Integer, String, Runnable> button1() {
                return button1;
            }

            public final DataGroup3<Integer, String, Runnable> button2() {
                return button2;
            }

            public final DataGroup3<Integer, String, Runnable> button3() {
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

    public void postPopMessage(int messageId) {
        Message m = new Message(messageId, true, new MessageType.Normal());
        postMessage(m);
    }

    public void postPopMessage(String message) {
        Message m = new Message(message, true, new MessageType.Normal());
        postMessage(m);
    }

    public void postPopMessage(int messageId, MessageType messageType) {
        Message m = new Message(messageId, true, messageType);
        postMessage(m);
    }

    public void postPopMessage(String message, MessageType messageType) {
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
        runOnBackgroundThread(null, task, 0, dispatchResultOnUIThread, onFinish);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, long delay, boolean dispatchResultOnUIThread, ResultCallback<T> onFinish) {
        runOnBackgroundThread(null, task, delay, dispatchResultOnUIThread, onFinish);
    }

    public <T> void runOnBackgroundThread(String name, ActionCallback0<T> task, boolean dispatchResultOnUIThread, ResultCallback<T> onFinish) {
        runOnBackgroundThread(name, task, 0, dispatchResultOnUIThread, onFinish);
    }

    public <T> void runOnBackgroundThread(String name, ActionCallback0<T> task, long delay, boolean dispatchResultOnUIThread, ResultCallback<T> onFinish) {
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

        Runnable startThread = () -> {
            if (TextUtils.isEmpty(name)) {
                new Thread(target).start();
            } else {
                new Thread(target, name).start();
            }
        };

        if (delay > 0) {
            runOnUIThread(startThread, delay);
        } else {
            startThread.run();
        }
    }

    public void runOnBackgroundThread(Runnable task) {
        runOnBackgroundThread(task, 0);
    }

    public void runOnBackgroundThread(Runnable task, long delay) {
        if (task == null) return;

        runOnBackgroundThread(
                "",

                //task
                () -> {
                    task.run();
                    return null;
                },

                //delay
                delay,

                //dispatchResultOnUIThread
                false,

                //onFinish
                null
        );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}