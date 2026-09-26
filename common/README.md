# libGDX Web Sockets — common

Desktop, Android and iOS backend for [libGDX Web Sockets](../README.md), based on
[nv-websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client).

```groovy
implementation "com.github.IvanVatov.gdx-websockets:common:$wsVersion"
```

Call `CommonWebSockets.initiate()` before creating web sockets:

```java
CommonWebSockets.initiate();
new Lwjgl3Application(new MyGame(), config);
```
