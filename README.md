# Rebro
Realm Browser for Android Studio

## Install
#### 1. Your App
```
repositories {
    maven {
        url "http://dl.bintray.com/ghedeon/maven"
    }
}

...

dependencies {
    debugCompile 'com.ghedeon:rebro-client:0.1'
}    
```
#### 2. Android Studio
Download latest release of `Rebro.zip` and install it via
Settings → Plugins → Install plugin from disk…

## Usage
No additional setup is required. Click "+" button in order to initiate the communication.

## Implementation Details
Websocket connection is used as a transport layer of app ⟺ plugin communication.

The ```rebro-client``` library is running a ws client that connects to the ws server owned by Rebro plugin.

A broadcast intent is send by Rebro plugin via ADB in order to communicate the server's IP to potential clients.

_Alternatively, an UDP autodiscovering technique might be used in order to identify the WS Server._

JSON RPC (backed by json-smart) is used in order to formalize the protocol and handle serialization/deserialization routine.

![](rebro_table.png)

### Libraries used
WebSockets: https://github.com/TooTallNate/Java-WebSocket

JSON RPC: http://software.dzhuvinov.com/json-rpc-2.0-base.html

Ideally, a smaller subset of RFC 6455 and JSON-RPC 2.0 Specification can be implemented, in order to reduce the number of dependencies.

## License
GNU GPLv3

http://www.gnu.org/licenses/gpl-3.0.txt
