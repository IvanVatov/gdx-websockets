# libGDX Web Sockets — teavm

Web backend for [libGDX Web Sockets](../README.md) on [gdx-teavm](https://github.com/xpenatan/gdx-teavm),
built on the browser `WebSocket` through TeaVM JSO. Frames are received as `ArrayBuffer` (binary) or
string (text).

```groovy
implementation "com.github.IvanVatov.gdx-websockets:teavm:$wsVersion"
```

TeaVM itself is a `compileOnly` dependency — the version comes from your gdx-teavm backend.

Call `TeaVMWebSockets.initiate()` in the TeaVM launcher before creating web sockets:

```java
TeaVMWebSockets.initiate();
new WebApplication(new MyGame(), config);
```
