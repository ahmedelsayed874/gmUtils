package gmutils;

import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class MessagingCenter {

    private static final Object lock = new Object();
    private static MessagingCenter _instance;

    public static MessagingCenter singleton() {
        synchronized (lock) {
            if (_instance == null) _instance = new MessagingCenter();
        }

        return _instance;
    }

    public static MessagingCenter createInstance() {
        MessagingCenter instance = null;

        synchronized (lock) {
            instance = new MessagingCenter();
        }

        return instance;
    }


    //----------------------------------------------------------------------------------------------

    private static class MessageKey {
        String callerId;
        String messageName;
        boolean hasLongLife;

        public MessageKey(Object caller, String messageName, boolean hasLongLife) {
            this.callerId = generateCallerId(caller);
            this.messageName = messageName;
            this.hasLongLife = hasLongLife;
        }

        private static String generateCallerId(Object caller) {
            return caller.getClass().getName() + caller.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MessageKey that = (MessageKey) o;
            return hasLongLife == that.hasLongLife &&
                    TextUtils.equals(callerId, that.callerId) &&
                    TextUtils.equals(messageName, that.messageName);
        }

        @Override
        public int hashCode() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Objects.hash(callerId, messageName, hasLongLife);
            } else {
                return (callerId + "+" + messageName).hashCode() + (hasLongLife ? 1 : 0);
            }
        }
    }

    public interface Observer {
        void onObserver(String messageName, Object data);
    }

    private final Map<MessageKey, Observer> observers = new HashMap<>();

    //----------------------------------------------------------------------------------------------

    public void subscribeOnce(Object caller, String messageName, Observer observer) {
        observers.put(new MessageKey(caller, messageName, false), observer);
    }

    public void subscribeOnce(Object caller, Class<?> dataType, Observer observer) {
        subscribeOnce(caller, dataType.getName(), observer);
    }

    public void subscribeAlways(Object caller, String messageName, Observer observer) {
        observers.put(new MessageKey(caller, messageName, true), observer);
    }

    public void subscribeAlways(Object caller, Class<?> dataType, Observer observer) {
        subscribeAlways(caller, dataType.getName(), observer);
    }

    //----------------------------------------------------------------------------------------------

    public void unsubscribe(Object caller) {
        List<MessageKey> messageKeys = new ArrayList<>();

        for (MessageKey messageKey : observers.keySet()) {
            if (messageKey.callerId.equals(MessageKey.generateCallerId(caller))) {
                messageKeys.add(messageKey);
            }
        }

        for (MessageKey messageKey : messageKeys) {
            observers.remove(messageKey);
        }
    }

    public void unsubscribe(Object caller, String messageName) {
        observers.remove(new MessageKey(caller, messageName, true));
    }

    public void unsubscribe(Object caller, Class<?> dataType) {
        unsubscribe(caller, dataType.getName());
    }

    //----------------------------------------------------------------------------------------------

    public void post(Class<?> dataType, Object data) {
        post(dataType.getName(), data);
    }

    public void post(String messageName, Object data) {
        List<MessageKey> consumedMessages = new ArrayList<>();

        for (MessageKey it : observers.keySet()) {
            if (("" + it.messageName).equals(messageName)) {
                Observer observer = observers.get(it);
                if (observer != null) observer.onObserver(messageName, data);

                if (!it.hasLongLife) {
                    observers.remove(it);
                    consumedMessages.add(it);
                }
            }
        }

        if (consumedMessages.size() != 0) {
            for (MessageKey it : consumedMessages) {
                observers.remove(it);
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    public void clearObservers() {
        for (MessageKey it : observers.keySet()) {
            observers.remove(it);
        }

        observers.clear();
    }
}
