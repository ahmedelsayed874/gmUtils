package gmutils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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
public class LooperThread extends Thread {
    public static class MessageArgs {
        private final Message msg;
        private final int handledMessageCount;
        private final int totalMessageCount;

        private MessageArgs(Message msg, int handledMessageCount, int totalMessageCount) {
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

        public int getTotalMessageCount() {
            return totalMessageCount;
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
                handler.onMessageHandled(new MessageArgs(msg, handledMsgCount, totalMsgCount));
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
        public void handleMessage(@NonNull Message msg) {
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


