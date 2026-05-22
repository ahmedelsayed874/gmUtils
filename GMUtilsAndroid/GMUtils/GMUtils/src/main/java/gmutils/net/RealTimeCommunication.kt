package gmutils.net

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import gmutils.logger.LoggerAbs
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlin.concurrent.thread

abstract class RealTimeCommunication(
    /**
     * @param url The WebSocket server URL (e.g., "ws://your.server.com/path").
     */
    val url: String,
    observerOwner: LifecycleOwner?,
    connectionStatusObserver: Observer<SocketEvent>?,
    messagesObserver: Observer<Message>?,
    val logger: LoggerAbs
) {
    internal val _connectionEvents = MutableLiveData<SocketEvent>()
    internal val _messages = MutableLiveData<Message>()

    //------------------------------------------------------------------------

    init {
        logger.printMethod { "url: $url" }

        connectionStatusObserver?.let {
            if (observerOwner == null) {
                _connectionEvents.observeForever(connectionStatusObserver)
            }
            //
            else {
                _connectionEvents.observe(observerOwner, connectionStatusObserver)
            }
        }

        messagesObserver?.let {
            if (observerOwner == null) {
                _messages.observeForever(messagesObserver)
            }
            //
            else {
                _messages.observe(observerOwner, messagesObserver)
            }
        }
    }

    //------------------------------------------------------------------------

    val messages: LiveData<Message> get() = _messages
    val connectionEvents: LiveData<SocketEvent> get() = _connectionEvents

    abstract fun start()

    abstract fun sendMessage(methodName: String, message: String): Boolean

    abstract fun stop()
}

sealed class SocketEvent {
    data class Connected(val obj: RealTimeCommunication) : SocketEvent()
    data class Disconnected(val code: Int, val reason: String) : SocketEvent()
    data class Error(val message: String) : SocketEvent()
}

data class Message(
    val obj: RealTimeCommunication,
    val source: String?,
    val content: String,
)

///////////////////////////////////////////////////////////////////////////////////////////////////

///implementation "com.squareup.okhttp3:okhttp3:4.12.0"
class RealTimeCommunicationOkHttp(
    url: String,
    observerOwner: LifecycleOwner?,
    connectionStatusObserver: Observer<SocketEvent>?,
    messagesObserver: Observer<Message>?,
    logger: LoggerAbs
) : RealTimeCommunication(url, observerOwner, connectionStatusObserver, messagesObserver, logger) {
    var webSocket: WebSocket? = null
        private set
    private val client = OkHttpClient()

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            logger.print { "RealTimeCommunication --> WebSocket connection opened" }
            // Emit a connection success event
            _connectionEvents.postValue(
                SocketEvent.Connected(this@RealTimeCommunicationOkHttp)
            )
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            logger.print { "RealTimeCommunication --> Received message: $text" }
            // Emit the received message to the flow
            _messages.postValue(
                Message(
                    this@RealTimeCommunicationOkHttp,
                    source = null,
                    content = text
                )
            )
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            logger.print { "RealTimeCommunication --> Received bytes: ${bytes.hex()}" }
            // You can handle byte messages if needed, e.g., convert to String
            _messages.postValue(
                Message(
                    this@RealTimeCommunicationOkHttp,
                    source = null,
                    content = bytes.utf8()
                )
            )
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            logger.print { "RealTimeCommunication --> WebSocket closing: $code / $reason" }
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            logger.print { "RealTimeCommunication --> WebSocket connection closed: $code / $reason" }
            this@RealTimeCommunicationOkHttp.webSocket = null
            // Emit a disconnection event
            _connectionEvents.postValue(SocketEvent.Disconnected(code, reason))
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            logger.print { "RealTimeCommunication --> WebSocket failure --> EXCEPTION:: $t" }
            this@RealTimeCommunicationOkHttp.webSocket = null
            _connectionEvents.postValue(SocketEvent.Error(t.message ?: "Unknown error"))
        }
    }

    /**
     * Starts the WebSocket connection.
     */
    override fun start() {
        logger.printMethod()

        if (webSocket != null) {
            logger.print { "RealTimeCommunication --> WebSocket is already connected." }
            return
        }

        try {
            val request = Request.Builder().url(url).build()
            webSocket = client.newWebSocket(request, webSocketListener)
            // The OkHttp client will keep the connection alive.
        } catch (e: Exception) {
            logger.print { "start() ----> Exception: $e" }
            _connectionEvents.postValue(
                SocketEvent.Error(
                    e.message ?: "Couldn't establish connection"
                )
            )
        }
    }

    /**
     * Sends a message to the WebSocket server.
     * @param message The string message to send.
     * @return True if the message was sent, false otherwise.
     */
    override fun sendMessage(methodName: String, message: String): Boolean {
        val b = webSocket?.send(message) ?: run {
            logger.print { "RealTimeCommunication --> Cannot send message, WebSocket is not connected." }
            false
        }

        logger.printMethod { "message: $message -----> return $b" }

        return b
    }

    /**
     * Closes the WebSocket connection gracefully.
     */
    override fun stop() {
        logger.printMethod()

        webSocket?.close(1000, "Client disconnected")
        webSocket = null

        // Optionally trigger a manual shutdown of the client if it's no longer needed elsewhere
        // client.dispatcher.executorService.shutdown()
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////

///implementation 'com.microsoft.signalr:signalr:7.0.0'
///<uses-permission android:name="android.permission.INTERNET" />
///<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
class RealTimeCommunicationSignalR(
    url: String,
    val observedMethods: List<String>,
    observerOwner: LifecycleOwner?,
    connectionStatusObserver: Observer<SocketEvent>?,
    messagesObserver: Observer<Message>?,
    logger: LoggerAbs
) : RealTimeCommunication(url, observerOwner, connectionStatusObserver, messagesObserver, logger) {
    var hubConnection: HubConnection? = null
        private set

    init {
        try {
            Class.forName("com.microsoft.signalr.HubConnection")
        } catch (_: Exception) {
            throw ClassNotFoundException(
                "Add this dependency to build.gradle: " +
                        "implementation 'com.microsoft.signalr:signalr:7.0.0'"
            )
        }
    }

    override fun start() {
        logger.printMethod {
            "url: $url, \n" +
                    "observedMethods: $observedMethods"
        }

        thread {
            hubConnection = HubConnectionBuilder.create(url).build()

            try {
                hubConnection!!.start().blockingAwait()

                // Register a handler for the methods called by the server
                observedMethods.forEach { observedMethod ->
                    hubConnection!!.on(
                        observedMethod,
                        Action1 { message ->
                            logger.print { "RealTimeCommunication --> new message of ($observedMethod) ---> $message" }
                            _messages.postValue(Message(this, source = null, content = message))
                        },
                        String::class.java
                    )
                }

                _connectionEvents.postValue(SocketEvent.Connected(this))
            } catch (e: java.lang.Exception) {
                // Handle connection error
                logger.print { "RealTimeCommunication.start() ----> Exception: $e" }
                _connectionEvents.postValue(
                    SocketEvent.Error(
                        e.message ?: "Couldn't establish connection"
                    )
                )
            }
        }
    }

    override fun sendMessage(methodName: String, message: String): Boolean {
        logger.printMethod()

        try {
            hubConnection?.send(methodName, message)
            return true
        } catch (e: Exception) {
            logger.print { "RealTimeCommunication.sendMessage ----> Exception: $e" }
            return false
        }
    }

    override fun stop() {
        logger.printMethod()

        hubConnection?.stop()
        hubConnection = null
    }
}
