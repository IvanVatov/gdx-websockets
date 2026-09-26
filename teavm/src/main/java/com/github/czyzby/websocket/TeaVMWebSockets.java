package com.github.czyzby.websocket;

import com.github.czyzby.websocket.WebSockets.WebSocketFactory;
import com.github.czyzby.websocket.impl.TeaVMWebSocket;

/** Allows to initiate TeaVM web sockets module. Web counterpart of {@link CommonWebSockets}. Call
 * {@link #initiate()} in the TeaVM launcher before creating web sockets. */
public class TeaVMWebSockets {
    private TeaVMWebSockets() {
    }

    /** Initiates {@link WebSocketFactory}. */
    public static void initiate() {
        WebSockets.FACTORY = new TeaVMWebSocketFactory();
    }

    /** Provides {@link TeaVMWebSocket} instances. */
    protected static class TeaVMWebSocketFactory implements WebSocketFactory {
        @Override
        public WebSocket newWebSocket(final String url) {
            return new TeaVMWebSocket(url);
        }
    }
}
