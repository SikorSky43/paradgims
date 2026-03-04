# Paradgims - TCP Group Chat

A Java-based TCP group chat project with:

- A server that accepts multiple clients, broadcasts messages, and tracks active users.
- A JavaFX server UI for logs and connected users.
- A JavaFX client application for joining and chatting.

## What This Project Does

Clients connect to a TCP server on a configured host/port (default `localhost:3000`), then exchange chat messages in real time.

Core behavior visible in the current code/artifacts:

- Server starts on a TCP port and waits for incoming clients.
- Each client joins with a username.
- Messages are broadcast to all connected clients.
- Join/leave events are announced.
- Connected user list is updated in the server UI.

## Project Structure

```text
dist/
  tcpserver-1.0-SNAPSHOT.jar   # runnable server jar
  tcpclient-1.0-SNAPSHOT.jar   # client jar (run with classpath)
TCPServer/tcpserver/src/main/
  java/model/ServerCore.java
  java/model/TestServer.java
  java/view/ServerApp.java
  resources/server.properties
uml/
  class-diagram.puml/.png
  deployment.puml/.png
```

## Prerequisites

- Java 17+
- Terminal access (macOS/Linux/Windows PowerShell equivalent commands)

For the client UI, if JavaFX is not already available in your runtime, install JavaFX SDK and use `--module-path` as shown below.

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/SikorSky43/paradgims.git
cd paradgims
```

### 2. Start the server

```bash
java -jar dist/tcpserver-1.0-SNAPSHOT.jar
```

Expected default port: `3000`

### 3. Start one or more clients

Client launch command (without JavaFX module-path):

```bash
java -cp dist/tcpclient-1.0-SNAPSHOT.jar view.ClientApp
```

If JavaFX modules are required on your machine:

```bash
java \
  --module-path /path/to/javafx-sdk/lib \
  --add-modules javafx.controls,javafx.graphics \
  -cp dist/tcpclient-1.0-SNAPSHOT.jar \
  view.ClientApp
```

Open multiple client instances to simulate a group chat.

## Configuration

- Server default port is defined in `server.properties` as `port=3000`.
- Client defaults are packaged as:
  - `host=localhost`
  - `port=3000`

## Troubleshooting

- `Address already in use`: another process is already using port `3000`; stop it or switch port.
- JavaFX errors like `Toolkit not initialized` / missing modules: launch client with JavaFX `--module-path` and `--add-modules`.
- Connection refused: make sure the server is started before launching clients.

## Documentation

- UML class diagram: `uml/class-diagram.png`
- UML deployment diagram: `uml/deployment.png`

