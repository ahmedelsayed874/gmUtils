package gmutils.ui.viewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gmutils.BackgroundTask;
import gmutils.StringSet;
import gmutils.collections.dataGroup.DataGroup2;
import gmutils.collections.dataGroup.DataGroup3;
import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;
import kotlin.Pair;


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

    public interface MessageDependent {
        MessageDependent appendMessage(Integer messageId);

        MessageDependent appendMessage(StringSet message);

        MessageDependent appendMessage(CharSequence message);

        int getMessagesCount();

        /**
         * returns the inserted message by "appendMessage"
         *
         * @param idx index
         * @return String Resource ID, StringSet, CharSequence
         */
        Object getMessage(int idx);
    }

    public interface ProgressStatus {
        class Show implements ProgressStatus, MessageDependent {
            private final List<Object> message; // StringSet | int

            public Show() {
                this(new ArrayList<>());
            }

            public Show(int messageId) {
                this(new ArrayList<>(List.of(messageId)));
            }

            public Show(StringSet message) {
                this(new ArrayList<>(List.of(message)));
            }

            private Show(List<Object> message) {
                this.message = message != null ? message : new ArrayList<>();
            }

            //------------------------------------------------------------------------------

            @Override
            public Show appendMessage(Integer messageId) {
                this.message.add(messageId);
                return this;
            }

            @Override
            public Show appendMessage(StringSet message) {
                this.message.add(message);
                return this;
            }

            @Override
            public Show appendMessage(CharSequence message) {
                this.message.add(message);
                return this;
            }

            @Override
            public int getMessagesCount() {
                if (message == null) return 0;
                return message.size();
            }

            /**
             * returns the inserted message by "appendMessage"
             *
             * @param idx index
             * @return String Resource ID, StringSet, CharSequence
             */
            @Override
            public Object getMessage(int idx) {
                if (message == null) return null;
                return message.get(idx);
            }

        }

        class Update extends Show {
            public Update() {
                super();
            }

            public Update(int messageId) {
                super(messageId);
            }

            public Update(StringSet message) {
                super(message);
            }

            //---------------------------------------------------------------------------------

            @Override
            public Update appendMessage(Integer messageId) {
                return (Update) super.appendMessage(messageId);
            }

            @Override
            public Update appendMessage(StringSet message) {
                return (Update) super.appendMessage(message);
            }

        }

        class Hide implements ProgressStatus {
        }
    }

    public static class Message implements MessageDependent {
        private final List<Object> messages; //Integer | StringSet
        public final boolean popup;
        public final MessageType type;
        private boolean enableOuterDismiss = true;

        public Message() {
            this(new ArrayList<>(), false, new MessageType.Normal());
        }

        public Message(boolean popup, MessageType type) {
            this(new ArrayList<>(), popup, type);
        }

        public Message(Integer messageId) {
            this(messageId == null ? null : new ArrayList<>(List.of(messageId)), false, new MessageType.Normal());
        }

        public Message(Integer messageId, boolean popup, MessageType type) {
            this(messageId == null ? null : new ArrayList<>(List.of(messageId)), popup, type);
        }

        public Message(CharSequence message) {
            this(message == null ? null : new ArrayList<>(List.of(message)), false, new MessageType.Normal());
        }

        public Message(CharSequence message, boolean popup, MessageType type) {
            this(message == null ? null : new ArrayList<>(List.of(message)), popup, type);
        }

        public Message(StringSet message) {
            this(message == null ? null : new ArrayList<>(List.of(message)), false, new MessageType.Normal());
        }

        public Message(StringSet message, boolean popup, MessageType type) {
            this(message == null ? null : new ArrayList<>(List.of(message)), popup, type);
        }

        private Message(List<Object> messages, boolean popup, MessageType type) {
            this.messages = messages != null ? messages : new ArrayList<>();
            this.popup = popup;
            this.type = type != null ? type : new MessageType.Normal();
        }

        //------------------------------------------------------------------------------

        @Override
        public Message appendMessage(Integer messageId) {
            this.messages.add(messageId);
            return this;
        }

        @Override
        public Message appendMessage(StringSet message) {
            this.messages.add(message);
            return this;
        }

        @Override
        public Message appendMessage(CharSequence message) {
            this.messages.add(message);
            return this;
        }

        @Override
        public int getMessagesCount() {
            if (messages == null) return 0;
            return messages.size();
        }

        /**
         * returns the inserted message by "appendMessage"
         *
         * @param idx index
         * @return String Resource ID, StringSet, CharSequence
         */
        @Override
        public Object getMessage(int idx) {
            if (messages == null) return null;
            return messages.get(idx);
        }

        //----------------------------------------------------------------------

        public Message setEnableOuterDismiss(boolean enableOuterDismiss) {
            this.enableOuterDismiss = enableOuterDismiss;
            return this;
        }

        public boolean isEnableOuterDismiss() {
            return enableOuterDismiss;
        }
    }

    public interface MessageType {
        void destroy();

        class Normal implements MessageType {
            private int iconRes;
            private DataGroup3<Integer, StringSet, Runnable> button1;
            private DataGroup3<Integer, StringSet, Runnable> button2;
            private DataGroup3<Integer, StringSet, Runnable> button3;

            public Normal() {
                this(
                        (Pair<Integer, Runnable>) null,
                        (Pair<Integer, Runnable>) null,
                        (Pair<Integer, Runnable>) null
                );
            }

            public Normal(Integer buttonText, Runnable buttonAction) {
                this(new Pair<>(buttonText, buttonAction), null, null);
            }

            public Normal(StringSet buttonText, Runnable buttonAction) {
                this(new DataGroup2<>(buttonText, buttonAction), null, null);
            }

            public Normal(
                    Pair<Integer, Runnable> button1,
                    Pair<Integer, Runnable> button2,
                    Pair<Integer, Runnable> button3
            ) {
                this.button1 = button1 == null ? null : new DataGroup3<>(button1.getFirst(), null, button1.getSecond());
                this.button2 = button2 == null ? null : new DataGroup3<>(button2.getFirst(), null, button2.getSecond());
                this.button3 = button3 == null ? null : new DataGroup3<>(button3.getFirst(), null, button3.getSecond());
            }

            public Normal(
                    DataGroup2<StringSet, Runnable> button1,
                    DataGroup2<StringSet, Runnable> button2,
                    DataGroup2<StringSet, Runnable> button3
            ) {
                this.button1 = button1 == null ? null : new DataGroup3<>(null, button1.value1, button1.value2);
                this.button2 = button2 == null ? null : new DataGroup3<>(null, button2.value1, button2.value2);
                this.button3 = button3 == null ? null : new DataGroup3<>(null, button3.value1, button3.value2);
            }

            //------------------------------------------------------

            public Normal setIconRes(int iconRes) {
                this.iconRes = iconRes;
                return this;
            }

            public int getIconRes() {
                return iconRes;
            }

            //------------------------------------------------------

            public boolean hasSpecialButtons() {
                return button1 != null || button2 != null || button3 != null;
            }

            public final DataGroup3<Integer, StringSet, Runnable> button1() {
                return button1;
            }

            public final DataGroup3<Integer, StringSet, Runnable> button2() {
                return button2;
            }

            public final DataGroup3<Integer, StringSet, Runnable> button3() {
                return button3;
            }

            @Override
            public void destroy() {
                button1 = null;
                button2 = null;
                button3 = null;
            }
        }

        class Error extends Normal {
            public Error() {
            }

            public Error(Integer buttonText, Runnable buttonAction) {
                super(buttonText, buttonAction);
            }

            public Error(StringSet buttonText, Runnable buttonAction) {
                super(buttonText, buttonAction);
            }

            public Error(Pair<Integer, Runnable> button1, Pair<Integer, Runnable> button2, Pair<Integer, Runnable> button3) {
                super(button1, button2, button3);
            }

            public Error(DataGroup2<StringSet, Runnable> button1, DataGroup2<StringSet, Runnable> button2, DataGroup2<StringSet, Runnable> button3) {
                super(button1, button2, button3);
            }
        }

        class Retry implements MessageType {
            private int iconRes;
            private Runnable _onRetry;

            public Retry(Runnable onRetry) {
                this._onRetry = onRetry;
            }

            //-----------------------------------------------------------

            public Retry setIconRes(int iconRes) {
                this.iconRes = iconRes;
                return this;
            }

            public int getIconRes() {
                return iconRes;
            }

            //-----------------------------------------------------------

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

    public void postPopMessage(CharSequence message) {
        Message m = new Message(message, true, new MessageType.Normal());
        postMessage(m);
    }

    public void postPopMessage(int messageId, MessageType messageType) {
        Message m = new Message(messageId, true, messageType);
        postMessage(m);
    }

    public void postPopMessage(CharSequence message, MessageType messageType) {
        Message m = new Message(message, true, messageType);
        postMessage(m);
    }

    public void postToastMessage(int messageId, boolean error) {
        Message m = new Message(messageId, false, error ? new MessageType.Error() : new MessageType.Normal());
        postMessage(m);
    }

    public void postToastMessage(CharSequence message, boolean error) {
        Message m = new Message(message, false, error ? new MessageType.Error() : new MessageType.Normal());
        postMessage(m);
    }

    public void postRetryMessage(CharSequence message, Runnable onRetry) {
        Message m = new Message(message, true, new MessageType.Retry(onRetry));
        postMessage(m);
    }

    //----------------------------------------------------------------------------------------------

    public void runOnUiThread(Runnable runnable) {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void runOnUiThread(Runnable runnable, long delay) {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }

    //----------------------------------------------------------

    public static class BackgroundThreadArgs<T> {
        private ActionCallback0<T> task;
        private ResultCallback<T> onFinish;
        private ResultCallback<Throwable> onException;
        private boolean dispatchResultOnUIThread;
        private long delay;
        private String threadName;

        public BackgroundThreadArgs(ActionCallback0<T> task) {
            this.task = task;
        }

        public BackgroundThreadArgs<T> setOnFinish(ResultCallback<T> onFinish, boolean dispatchResultOnUIThread) {
            this.onFinish = onFinish;
            this.dispatchResultOnUIThread = dispatchResultOnUIThread;
            return this;
        }

        public BackgroundThreadArgs<T> setOnException(ResultCallback<Throwable> onException) {
            this.onException = onException;
            return this;
        }

        public BackgroundThreadArgs<T> setDelay(long delay) {
            this.delay = delay;
            return this;
        }

        public BackgroundThreadArgs<T> setThreadName(String threadName) {
            this.threadName = threadName;
            return this;
        }
    }

    public <T> void runOnBackgroundThread(BackgroundThreadArgs<T> args) {
        if (args.task == null) return;

        Runnable target = () -> {
            T result;
            if (args.onException == null) {
                result = args.task.invoke();
            } else {
                try {
                    result = args.task.invoke();
                } catch (Throwable e) {
                    if (args.dispatchResultOnUIThread)
                        runOnUiThread(() -> args.onException.invoke(e));
                    else
                        args.onException.invoke(e);

                    return;
                }
            }

            if (args.onFinish != null) {
                if (args.dispatchResultOnUIThread)
                    runOnUiThread(() -> args.onFinish.invoke(result));
                else
                    args.onFinish.invoke(result);
            }
        };

        Runnable startThread = () -> {
            if (TextUtils.isEmpty(args.threadName)) {
                new Thread(target).start();
            } else {
                new Thread(target, args.threadName).start();
            }
        };

        if (args.delay > 0) {
            runOnUiThread(startThread, args.delay);
        } else {
            startThread.run();
        }
    }

    //---------------------------------------------------------

    public void runOnBackgroundThread(Runnable task) {
        runOnBackgroundThread(task, 0, null);
    }

    public void runOnBackgroundThread(Runnable task, long delay) {
        runOnBackgroundThread(task, delay, null);
    }

    public void runOnBackgroundThread(Runnable task, long delay, ResultCallback<Throwable> onException) {
        if (task == null) return;

        runOnBackgroundThread(new BackgroundThreadArgs<>(
                        //task
                        () -> {
                            task.run();
                            return null;
                        }
                ).setDelay(delay).setOnException(onException)
        );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}