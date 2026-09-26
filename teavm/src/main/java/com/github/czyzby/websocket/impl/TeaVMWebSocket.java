package com.github.czyzby.websocket.impl;

import com.github.czyzby.websocket.WebSockets;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.data.WebSocketState;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int8Array;

/** Default web socket implementation for TeaVM applications, built on top of the browser WebSocket
 * exposed through TeaVM JSO. Hostname verification and TCP_NODELAY are handled by the browser. */
public class TeaVMWebSocket extends AbstractWebSocket {

    private org.teavm.jso.websocket.WebSocket ws;

    public TeaVMWebSocket(final String url) {
        super(url);
    }

    @Override
    public void connect() throws WebSocketException {
        if (isOpen() || isConnecting()) {
            close(WebSockets.ABNORMAL_AUTOMATIC_CLOSE_CODE);
        }
        try {
            open(super.getUrl());
        } catch (final Throwable exception) {
            throw new WebSocketException("Unable to open the web socket.", exception);
        }
    }

    /** @param url used to create the web socket. */
    protected void open(final String url) {
        if (url == null) {
            throw new WebSocketException("URL cannot be null.");
        }
        try {
            if (ws != null) {
                ws.close(WebSocketCloseCode.AWAY);
            }
            ws = new org.teavm.jso.websocket.WebSocket(url);
            ws.setBinaryType("arraybuffer");
            ws.onOpen(event -> postOpenEvent());
            ws.onClose(event -> postCloseEvent(event.getCode(), event.getReason()));
            ws.onError(event -> postErrorEvent(new WebSocketException(
                    "An error occurred. Error type: " + event.getType())));
            ws.onMessage(event -> onMessage(event.getData()));
        } catch (final Throwable exception) {
            throw new WebSocketException("Unable to connect.", exception);
        }
    }

    /** Invoked by native listener.
     *
     * @param data received frame — either a JS string or an ArrayBuffer. */
    protected void onMessage(final JSObject data) {
        if (isString(data)) {
            final String message = asString(data);
            if (message != null && message.length() > 0) {
                postMessageEvent(message);
            }
        } else {
            final byte[] message = toInt8Array(data).copyToJavaArray();
            if (message.length > 0) {
                postMessageEvent(message);
            }
        }
    }

    @Override
    public WebSocketState getState() {
        if (ws == null) {
            return WebSocketState.CLOSED;
        }
        try {
            return WebSocketState.getById(ws.getReadyState());
        } catch (final Throwable exception) {
            // Might be thrown if invalid state, for some reason.
            postErrorEvent(exception);
            return WebSocketState.CLOSED;
        }
    }

    @Override
    public void close(final int closeCode, final String reason) throws WebSocketException {
        WebSocketCloseCode.checkIfAllowedInClient(closeCode);
        if (ws == null) {
            return;
        }
        try {
            if (reason == null) {
                ws.close(closeCode);
            } else {
                ws.close(closeCode, reason);
            }
        } catch (final Throwable exception) {
            throw new WebSocketException("Unable to close the web socket.", exception);
        }
    }

    @Override
    protected void sendBinary(final byte[] message) {
        if (ws == null) {
            return;
        }
        try {
            ws.send(Int8Array.copyFromJavaArray(message));
        } catch (final Throwable exception) {
            throw new WebSocketException(exception);
        }
    }

    @Override
    protected void sendString(final String message) {
        if (ws == null) {
            return;
        }
        try {
            ws.send(message);
        } catch (final Throwable exception) {
            throw new WebSocketException(exception);
        }
    }

    @Override
    public boolean isSupported() {
        return org.teavm.jso.websocket.WebSocket.isSupported();
    }

    @Override
    public String getUrl() {
        return ws == null ? super.getUrl() : ws.getUrl();
    }

    @JSBody(params = "data", script = "return typeof data === 'string';")
    private static native boolean isString(JSObject data);

    @JSBody(params = "data", script = "return data;")
    private static native String asString(JSObject data);

    @JSBody(params = "data", script = "return new Int8Array(data);")
    private static native Int8Array toInt8Array(JSObject data);
}
