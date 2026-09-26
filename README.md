# libGDX Web Sockets

Client-side web sockets for libGDX applications on desktop, Android and the web (TeaVM).

Fork of [czyzby's websockets](https://github.com/czyzby/gdx-lml/tree/master/websocket) via
[MrStahlfelge/gdx-websockets](https://github.com/MrStahlfelge/gdx-websockets), trimmed down to a
plain text/binary socket API with a TeaVM backend.

## Modules

| Module   | Platforms                   | Implementation                                                                 |
|----------|-----------------------------|--------------------------------------------------------------------------------|
| `core`   | shared (libGDX core project) | `WebSocket`, `WebSocketListener`, `WebSockets` API                            |
| `common` | desktop, Android, iOS       | [nv-websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client) |
| `teavm`  | web ([gdx-teavm](https://github.com/xpenatan/gdx-teavm)) | browser `WebSocket` through TeaVM JSO             |

## Dependencies

Artifacts are built by [JitPack](https://jitpack.io). Add the repository to the root `build.gradle`:

```groovy
maven { url 'https://jitpack.io' }
```

Set the version in `gradle.properties`:

```properties
wsVersion=1.11.0
```

Core project:

```groovy
api "com.github.IvanVatov.gdx-websockets:core:$wsVersion"
```

Desktop / Android / iOS launcher projects:

```groovy
implementation "com.github.IvanVatov.gdx-websockets:common:$wsVersion"
```

TeaVM launcher project:

```groovy
implementation "com.github.IvanVatov.gdx-websockets:teavm:$wsVersion"
```

The `teavm` module only declares TeaVM (`teavm-jso-apis`) as `compileOnly`; the TeaVM version comes
from your gdx-teavm backend. It is built against TeaVM 0.14.

## Usage

### Initialization

Call the platform's `initiate()` in the launcher, before any web socket is created.

Desktop / Android / iOS:

```java
CommonWebSockets.initiate();
new Lwjgl3Application(new MyGame(), config);
```

TeaVM:

```java
TeaVMWebSockets.initiate();
new WebApplication(new MyGame(), config);
```

### Connecting to a server

```java
WebSocket socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl(host, 443, "websocket"));
socket.setSendGracefully(true);
socket.addListener(new WebSocketListener() { ... });
socket.connect();

socket.send(bytes);   // binary frame
socket.send(text);    // text frame
```

Listener callbacks run on the socket's thread on desktop/Android — use `Gdx.app.postRunnable` to get
back to the render thread. On the web they run on the browser's event loop.

`setVerifyHostname` and `setUseTcpNoDelay` apply to `common` only; in the browser both are handled by
the browser itself.

## Changes

### 1.11.0

- **Added** `teavm` module — web socket backend for gdx-teavm (`TeaVMWebSockets.initiate()`).
- **Removed** `html` (GWT) module and the `.gwt.xml` module descriptors.
- **Removed** `serialization` module.
- **Removed** the object serialization layer from `core`: `WebSocket#send(Object)`,
  `setSerializer`/`getSerializer`/`setSerializeAsString`, `WebSockets.DEFAULT_SERIALIZER`, the
  `serialization` package, `AbstractWebSocketListener`, `WebSocketAdapter` and `WebSocketHandler`.
  Serialize to `byte[]` or `String` yourself and implement `WebSocketListener` directly.
- **Fixed** `NvWebSocket#setVerifyHostname` had no effect after construction; hostname verification
  now defaults to `true`.
- Built with Java 17 and libGDX 1.14.2.
