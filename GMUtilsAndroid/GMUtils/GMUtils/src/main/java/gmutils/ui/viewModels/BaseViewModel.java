package gmutils.ui.viewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        /**
         * @param messages int - CharSequence - StringSet
         */
        static ProgressStatus show(List<Object> messages) {
            return new Show(messages);
        }

        static ProgressStatus update(StringSet message) {
            return new Update(message);
        }

        static ProgressStatus hide(boolean forceHide) {
            return new Hide(forceHide);
        }

        class Show implements ProgressStatus, MessageDependent {
            private final List<Object> message; // StringSet | int

            public Show() {
                this(new ArrayList<>());
            }

            public Show(int messageId) {
                this(new ArrayList<>(List.of(messageId)));
                assert messageId > 0;
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
                assert messageId > 0;
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
                assert messageId > 0;
            }

            public Update(StringSet message) {
                super(message);
            }

            //---------------------------------------------------------------------------------

            @Override
            public Update appendMessage(Integer messageId) {
                assert messageId > 0;
                return (Update) super.appendMessage(messageId);
            }

            @Override
            public Update appendMessage(StringSet message) {
                return (Update) super.appendMessage(message);
            }

        }

        class Hide implements ProgressStatus {
            public final boolean forceHide;

            public Hide() {
                this(false);
            }

            public Hide(boolean forceHide) {
                this.forceHide = forceHide;
            }
        }
    }

    public static class Message implements MessageDependent {
        private final List<Object> messages; //Integer | StringSet
        public final MessageType type;
        private boolean enableOuterDismiss = true;

        public Message() {
            this(new ArrayList<>(), new MessageType.Hint());
        }

        public Message(MessageType type) {
            this(new ArrayList<>(), type);
        }

        public Message(Integer messageId) {
            this(messageId, new MessageType.Hint());
        }

        public Message(Integer messageId, MessageType type) {
            this(messageId == null || messageId <= 0 ? null : new ArrayList<>(List.of(messageId)), type);
        }

        public Message(CharSequence message) {
            this(message == null ? null : new ArrayList<>(List.of(message)), new MessageType.Hint());
        }

        public Message(CharSequence message, MessageType type) {
            this(message == null ? null : new ArrayList<>(List.of(message)), type);
        }

        public Message(StringSet message) {
            this(message == null ? null : new ArrayList<>(List.of(message)), new MessageType.Hint());
        }

        public Message(StringSet message, MessageType type) {
            this(message == null ? null : new ArrayList<>(List.of(message)), type);
        }

        private Message(List<Object> messages, MessageType type) {
            this.messages = messages != null ? messages : new ArrayList<>();
            this.type = type != null ? type : new MessageType.Hint();
        }

        //------------------------------------------------------------------------------

        @Override
        public Message appendMessage(Integer messageId) {
            assert messageId > 0;
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
        boolean isErrorMessage();

        void destroy();

        class Dialog implements MessageType {
            private int iconRes;
            private DataGroup3<Integer, StringSet, Runnable> button1;
            private DataGroup3<Integer, StringSet, Runnable> button2;
            private DataGroup3<Integer, StringSet, Runnable> button3;
            private boolean error;

            public Dialog() {
                this(false);
            }

            public Dialog(boolean error) {
                this(
                        (Pair<Integer, Runnable>) null,
                        (Pair<Integer, Runnable>) null,
                        (Pair<Integer, Runnable>) null,
                        error
                );
            }

            public Dialog(Integer buttonText, Runnable buttonAction) {
                this(new Pair<>(buttonText, buttonAction), null, null, false);
            }

            public Dialog(Integer buttonText, Runnable buttonAction, boolean error) {
                this(new Pair<>(buttonText, buttonAction), null, null, error);
            }

            public Dialog(StringSet buttonText, Runnable buttonAction) {
                this(new DataGroup2<>(buttonText, buttonAction), null, null, false);
            }

            public Dialog(StringSet buttonText, Runnable buttonAction, boolean error) {
                this(new DataGroup2<>(buttonText, buttonAction), null, null, error);
            }

            public Dialog(
                    Pair<Integer, Runnable> button1,
                    Pair<Integer, Runnable> button2,
                    Pair<Integer, Runnable> button3
            ) {
                this(button1, button2, button3, false);
            }

            public Dialog(
                    Pair<Integer, Runnable> button1,
                    Pair<Integer, Runnable> button2,
                    Pair<Integer, Runnable> button3,
                    boolean error
            ) {
                this.button1 = button1 == null ? null : new DataGroup3<>(button1.getFirst(), null, button1.getSecond());
                this.button2 = button2 == null ? null : new DataGroup3<>(button2.getFirst(), null, button2.getSecond());
                this.button3 = button3 == null ? null : new DataGroup3<>(button3.getFirst(), null, button3.getSecond());
                this.error = error;
            }

            public Dialog(
                    DataGroup2<StringSet, Runnable> button1,
                    DataGroup2<StringSet, Runnable> button2,
                    DataGroup2<StringSet, Runnable> button3
            ) {
                this(button1, button2, button3, false);
            }

            public Dialog(
                    DataGroup2<StringSet, Runnable> button1,
                    DataGroup2<StringSet, Runnable> button2,
                    DataGroup2<StringSet, Runnable> button3,
                    boolean error
            ) {
                this.button1 = button1 == null ? null : new DataGroup3<>(null, button1.value1, button1.value2);
                this.button2 = button2 == null ? null : new DataGroup3<>(null, button2.value1, button2.value2);
                this.button3 = button3 == null ? null : new DataGroup3<>(null, button3.value1, button3.value2);
                this.error = error;
            }

            //------------------------------------------------------

            public Dialog setIconRes(int iconRes) {
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

            @Override
            public boolean isErrorMessage() {
                return error;
            }
        }

        class Hint implements MessageType {
            public final boolean error;

            public Hint() {
                this(false);
            }

            public Hint(boolean error) {
                this.error = error;
            }

            @Override
            public void destroy() {
            }

            @Override
            public boolean isErrorMessage() {
                return error;
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

            @Override
            public boolean isErrorMessage() {
                return true;
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
        Message m = new Message(messageId, new MessageType.Dialog());
        postMessage(m);
    }

    public void postPopMessage(CharSequence message) {
        Message m = new Message(message, new MessageType.Dialog());
        postMessage(m);
    }

    public void postPopMessage(int messageId, MessageType.Dialog messageType) {
        Message m = new Message(messageId, messageType);
        postMessage(m);
    }

    public void postPopMessage(CharSequence message, MessageType.Dialog messageType) {
        Message m = new Message(message, messageType);
        postMessage(m);
    }

    public void postToastMessage(int messageId, boolean error) {
        Message m = new Message(messageId, new MessageType.Hint(error));
        postMessage(m);
    }

    public void postToastMessage(CharSequence message, boolean error) {
        Message m = new Message(message, new MessageType.Hint(error));
        postMessage(m);
    }

    public void postRetryMessage(CharSequence message, Runnable onRetry) {
        Message m = new Message(message, new MessageType.Retry(onRetry));
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

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish) {
        runOnBackgroundThread(task, onFinish, true, 0, null, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, boolean dispatchResultOnUIThread) {
        runOnBackgroundThread(task, onFinish, dispatchResultOnUIThread, 0, null, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, boolean dispatchResultOnUIThread, long delay) {
        runOnBackgroundThread(task, onFinish, dispatchResultOnUIThread, delay, null, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, boolean dispatchResultOnUIThread, long delay, ResultCallback<Throwable> onException) {
        runOnBackgroundThread(task, onFinish, dispatchResultOnUIThread, delay, onException, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, boolean dispatchResultOnUIThread, long delay, ResultCallback<Throwable> onException, String threadName) {
        if (task == null) return;

        Runnable target = () -> {
            T result;
            if (onException == null) {
                result = task.invoke();
            } else {
                try {
                    result = task.invoke();
                } catch (Throwable e) {
                    if (dispatchResultOnUIThread)
                        runOnUiThread(() -> onException.invoke(e));
                    else
                        onException.invoke(e);

                    return;
                }
            }

            if (onFinish != null) {
                if (dispatchResultOnUIThread)
                    runOnUiThread(() -> onFinish.invoke(result));
                else
                    onFinish.invoke(result);
            }
        };

        Runnable startThread = () -> {
            if (TextUtils.isEmpty(threadName)) {
                new Thread(target).start();
            } else {
                new Thread(target, threadName).start();
            }
        };

        if (delay > 0) {
            runOnUiThread(startThread, delay);
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

        runOnBackgroundThread(
                //task
                () -> {
                    task.run();
                    return null;
                },

                //onFinish
                null,

                //dispatchToUi
                true,

                //delay
                delay,

                //onException
                onException,

                //threadName
                null
        );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}