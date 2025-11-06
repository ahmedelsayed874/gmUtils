package gmutils.ui.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gmutils.StringSet;
import gmutils.backgroundWorkers.BackgroundTask;
import gmutils.backgroundWorkers.BackgroundTaskAbs;
import gmutils.collections.dataGroup.DataGroup2;
import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ResultCallback;
import gmutils.ui.utils.uihandler.UiHandlerAbs;


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
        MessageDependent appendMessage(int messageId);

        MessageDependent appendMessage(CharSequence message);

        MessageDependent appendMessage(StringSet message);

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
         * @param message int - CharSequence - StringSet
         */
        static ProgressStatus show(@Nullable Object message) {
            return new Show(Collections.singletonList(message));
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
                this(messageId <= 0 ? new ArrayList<>() : new ArrayList<>(List.of(messageId)));
            }

            public Show(CharSequence message) {
                this(new ArrayList<>(List.of(message)));
            }

            public Show(StringSet message) {
                this(new ArrayList<>(List.of(message)));
            }

            private Show(List<Object> message) {
                this.message = message != null ? message : new ArrayList<>();
            }

            //------------------------------------------------------------------------------

            @Override
            public Show appendMessage(int messageId) {
                if (messageId <= 0) return this;
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
            private Integer progress;

            public Update() {
                super();
            }

            public Update(int messageId) {
                super(messageId);
            }

            public Update(CharSequence message) {
                super(message);
            }

            public Update(StringSet message) {
                super(message);
            }

            //---------------------------------------------------------------------------------

            @Override
            public Update appendMessage(int messageId) {
                return (Update) super.appendMessage(messageId);
            }

            @Override
            public Update appendMessage(StringSet message) {
                return (Update) super.appendMessage(message);
            }

            //---------------------------------------------------------------------------------

            public Update setProgress(Integer progress) {
                this.progress = progress;
                return this;
            }

            public int getProgress() {
                return progress;
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

        public Message() {
            this(new ArrayList<>(), new MessageType.Hint());
        }

        public Message(int messageId, MessageType type) {
            this(messageId <= 0 ? null : new ArrayList<>(List.of(messageId)), type);
        }

        public Message(CharSequence message, MessageType type) {
            this(message == null ? null : new ArrayList<>(List.of(message)), type);
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
        public Message appendMessage(int messageId) {
            if (messageId <= 0) return this;
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

    }

    public interface MessageType {
        boolean isErrorMessage();

        void destroy();

        class Dialog implements MessageType {
            private int iconRes;
            private Object title;
            private DataGroup2<Object, Runnable> button1;
            private DataGroup2<Object, Runnable> button2;
            private DataGroup2<Object, Runnable> button3;
            private Runnable onDismiss;
            private final boolean error;
            private boolean enableOuterDismiss = true;

            public Dialog() {
                this(false);
            }

            public Dialog(boolean error) {
                this.error = error;
            }

            //------------------------------------------------------

            public Dialog setIconRes(int iconRes) {
                this.iconRes = iconRes;
                return this;
            }

            //----------------

            public Dialog setTitle(int title) {
                this.title = title;
                return this;
            }

            public Dialog setTitle(CharSequence title) {
                this.title = title;
                return this;
            }

            public Dialog setTitle(StringSet title) {
                this.title = title;
                return this;
            }

            //-----------------

            public Dialog setButton1(int title, Runnable action) {
                this.button1 = new DataGroup2<>(title, action);
                return this;
            }

            public Dialog setButton1(CharSequence title, Runnable action) {
                this.button1 = new DataGroup2<>(title, action);
                return this;
            }

            public Dialog setButton1(StringSet title, Runnable action) {
                this.button1 = new DataGroup2<>(title, action);
                return this;
            }

            //------------------

            public Dialog setButton2(int title, Runnable action) {
                this.button2 = new DataGroup2<>(title, action);
                return this;
            }

            public Dialog setButton2(CharSequence title, Runnable action) {
                this.button2 = new DataGroup2<>(title, action);
                return this;
            }

            public Dialog setButton2(StringSet title, Runnable action) {
                this.button2 = new DataGroup2<>(title, action);
                return this;
            }

            //-------------------

            public Dialog setButton3(int title, Runnable action) {
                this.button3 = new DataGroup2<>(title, action);
                return this;
            }

            public Dialog setButton3(CharSequence title, Runnable action) {
                this.button3 = new DataGroup2<>(title, action);
                return this;
            }

            public Dialog setButton3(StringSet title, Runnable action) {
                this.button3 = new DataGroup2<>(title, action);
                return this;
            }

            //-----------------

            public Dialog setOnDismiss(Runnable onDismiss) {
                this.onDismiss = onDismiss;
                return this;
            }

            public Dialog setEnableOuterDismiss(boolean enableOuterDismiss) {
                this.enableOuterDismiss = enableOuterDismiss;
                return this;
            }

            //-------------------------------------------------------------------------------

            public int getIconRes() {
                return iconRes;
            }

            public Object getTitle() {
                return title;
            }

            public boolean hasSpecialButtons() {
                return button1 != null || button2 != null || button3 != null;
            }

            public final DataGroup2<Object, Runnable> button1() {
                return button1;
            }

            public final DataGroup2<Object, Runnable> button2() {
                return button2;
            }

            public final DataGroup2<Object, Runnable> button3() {
                return button3;
            }

            public Runnable getOnDismiss() {
                return onDismiss;
            }

            public boolean isEnableOuterDismiss() {
                return enableOuterDismiss;
            }

            @Override
            public void destroy() {
                button1 = null;
                button2 = null;
                button3 = null;
                onDismiss = null;
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

    public BaseViewModel(@NotNull Application application) {
        super(application);
    }

    //----------------------------------------------------------------------------------------------

    private MutableLiveData<ProgressStatus> progressStatusLiveData;
    private MutableLiveData<Message> alertMessageLiveData;
    private MutableLiveData<String> updateUiLiveData;

    public MutableLiveData<ProgressStatus> progressStatusLiveData() {
        if (progressStatusLiveData == null) progressStatusLiveData = new MutableLiveData<>();
        return progressStatusLiveData;
    }

    public MutableLiveData<Message> alertMessageLiveData() {
        if (alertMessageLiveData == null) alertMessageLiveData = new MutableLiveData<>();
        return alertMessageLiveData;
    }

    public MutableLiveData<String> updateUiLiveData() {
        if (updateUiLiveData == null) updateUiLiveData = new MutableLiveData<>();
        return updateUiLiveData;
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

    public void postPopMessage(StringSet message) {
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

    public void postPopMessage(StringSet message, MessageType.Dialog messageType) {
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

    public void postToastMessage(StringSet message, boolean error) {
        Message m = new Message(message, new MessageType.Hint(error));
        postMessage(m);
    }

    public void postRetryMessage(CharSequence message, Runnable onRetry) {
        Message m = new Message(message, new MessageType.Retry(onRetry));
        postMessage(m);
    }

    public void postRetryMessage(StringSet message, Runnable onRetry) {
        Message m = new Message(message, new MessageType.Retry(onRetry));
        postMessage(m);
    }

    //----------------------------------------------------------------------------------------------

    private UiHandlerAbs uiHandler;

    @NotNull
    public UiHandlerAbs getHandler() {
        if (uiHandler == null) {
            uiHandler = UiHandlerAbs.getInstance();
        }

        return uiHandler;
    }

    public void runOnUiThread(Runnable runnable) {
        getHandler().post(0, runnable);
    }

    public void runOnUiThread(Runnable runnable, long delay) {
        getHandler().post((int) delay, runnable);
    }

    //----------------------------------------------------------

    private BackgroundTaskAbs backgroundTask;

    @NotNull
    public BackgroundTaskAbs getBackgroundTaskInstance(String threadName) {
        if (backgroundTask == null) {
            backgroundTask = new BackgroundTask(threadName);
        }

        return backgroundTask;
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish) {
        runOnBackgroundThread(task, onFinish, null, true, 0, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, ResultCallback<Throwable> onException) {
        runOnBackgroundThread(task, onFinish, onException, true, 0, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, ResultCallback<Throwable> onException, boolean dispatchResultOnUIThread, long delay) {
        runOnBackgroundThread(task, onFinish, onException, dispatchResultOnUIThread, delay, null);
    }

    public <T> void runOnBackgroundThread(ActionCallback0<T> task, ResultCallback<T> onFinish, ResultCallback<Throwable> onException, boolean dispatchResultOnUIThread, long delay, String threadName) {
        if (task == null) throw new IllegalArgumentException("task can't be null");

        Runnable job = () -> {
            getBackgroundTaskInstance(threadName).execute(task, onFinish, onException, dispatchResultOnUIThread);
        };

        if (delay > 0) {
            runOnUiThread(job, delay);
        }
        //
        else {
            job.run();
        }
    }

    //---------------------------------------------------------

    public void runOnBackgroundThread(Runnable task) {
        runOnBackgroundThread(task, 0, null);
    }

    /*public void runOnBackgroundThread(Runnable task, ResultCallback<Throwable> onException) {
        runOnBackgroundThread(task, 0, onException);
    }*/

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

                //onException
                onException,

                //dispatchToUi
                true,

                //delay
                delay,

                //threadName
                null
        );
    }

    //----------------------------------------------------------------------------------------------

    private boolean isCleared = false;

    public boolean isCleared() {
        return isCleared;
    }

    @Override
    protected void onCleared() {
        isCleared = true;
        super.onCleared();
    }

}
