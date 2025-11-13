package gmutils.backgroundWorkers;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.listeners.ValueGetter;

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
public class LooperThread extends Thread {
    public static class MessageArgs {
        private Message msg;
        private final int handledMessageCount;
        private ValueGetter<Integer> totalMessageCount;

        private MessageArgs(Message msg, int handledMessageCount, ValueGetter<Integer> totalMessageCount) {
            this.msg = msg;
            this.handledMessageCount = handledMessageCount;
            this.totalMessageCount = totalMessageCount;
        }

        public Message getMsg() {
            return msg;
        }

        public int getHandledMessageCount() {
            return handledMessageCount;
        }

        public ValueGetter<Integer> getTotalMessageCount() {
            return totalMessageCount;
        }

        private void destroy() {
            msg = null;
            totalMessageCount = null;
        }
    }

    public interface MessageHandler {
        //void onMessageHandled(Message msg, int handledMessageCount, int totalMessageCount);
        void onMessageHandled(MessageArgs args);
    }

    private MessageHandler onMessageHandled;

    private MyHandler handler = null;
    private final List<Message> unHandledMessages = new ArrayList<>();
    private int totalMsgCount = 0;
    private int handledMsgCount = 0;

    public LooperThread(MessageHandler onMessageHandled) {
        this(null, onMessageHandled);
    }

    public LooperThread(String threadName, MessageHandler onMessageHandled) {
        this.onMessageHandled = onMessageHandled;

        start();

        setName(TextUtils.isEmpty(threadName) ? getClass().getSimpleName() + hashCode() : threadName);
    }

    @Override
    public void run() {
        Looper.prepare();

        handler = new MyHandler(msg -> {
            MessageHandler handler = onMessageHandled;
            if (handler != null) {
                handledMsgCount++;

                MessageArgs args = new MessageArgs(msg, handledMsgCount, totalMessageCountGetter);
                handler.onMessageHandled(args);
                args.destroy();
            }
        });

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Message msg : unHandledMessages) {
            sendMessage(msg);
        }

        unHandledMessages.clear();

        Looper.loop();
    }

    private ValueGetter<Integer> totalMessageCountGetter = () -> totalMsgCount;

    public void sendMessage(Message msg) {
        if (handler != null) {
            totalMsgCount++;
            handler.sendMessage(msg);

        } else {
            unHandledMessages.add(msg);
        }
    }

    public void quit() {
        handler.destroy();
        handler = null;
        onMessageHandled = null;
        totalMessageCountGetter = null;
    }

    public boolean isRunning() {
        if (handler != null) {
            return !unHandledMessages.isEmpty() || handledMsgCount < totalMsgCount;
        }

        return false;
    }

    public Map<String, Object> report() {
        Map<String, Object> m = new HashMap<>();
        m.put("unhandledMessages", unHandledMessages);
        m.put("totalMessagesCount", totalMsgCount);
        m.put("handledMessagesCount", handledMsgCount);
        m.put("isRunning", isRunning());
        return m;
    }

    public static class MyHandler extends Handler {
        public interface MessageHandler {
            void onMessageHandled(Message msg);
        }

        private MessageHandler onMessageHandled;

        MyHandler(MessageHandler onMessageHandled) {
            super(Looper.myLooper());
            this.onMessageHandled = onMessageHandled;
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            if (onMessageHandled != null)
                onMessageHandled.onMessageHandled(msg);
        }

        void destroy() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    getLooper().quitSafely();
                } else {
                    getLooper().quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            onMessageHandled = null;
        }
    }
}


