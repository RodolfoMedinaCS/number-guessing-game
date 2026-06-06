# Multiplayer Number Guessing Game

A real time multiplayer number guessing game that runs in the terminal. Players connect to a shared server, pick a username, and race to guess the same secret number. Built from scratch using raw Java WebSockets.

---

## What it does

Multiple players connect to a live WebSocket server via terminal. Each player joins with a username, then starts guessing a number between 1 and 100. The server tells you if your guess is too high or too low. Everyone is guessing the **same number** at the same time & first one to get it wins, the server resets, and a new round starts immediately.

---

## Why I built this

This project is part of a bigger learning journey toward building a **real time collaborative note taking app**. Before touching Spring Boot or any framework, I wanted to understand WebSockets down to their roots, the raw protocol, the handshake, the frames, all of it. This game was my first project applying those fundamentals from scratch.

---

## The biggest thing that clicked

The hardest part was understanding **when the lifecycle annotations actually fire** & realizing I wasn't the one calling them. You don't call `@OnOpen` or `@OnMessage` yourself. You write the method, attach the annotation, and the framework calls it automatically when a client connects, sends a message, or closes their laptop. Once that clicked, the whole event driven model made sense.

---

## Tech Stack

- **Java 23**
- **Jakarta WebSocket API (JSR 356)** the raw WebSocket standard, no framework
- **Tyrus** — the reference implementation / server runtime
- **Maven**

---

## Architecture

The project is split into two classes with a clear separation of concerns:

**`GameEndpoint.java`** — handles all WebSocket communication. Manages connected sessions, routes incoming messages, and sends responses. Knows nothing about game rules.

**`NumberGame.java`** — handles all game logic. Picks the secret number, evaluates guesses, resets the game on a win. Knows nothing about WebSockets.

This split means the game logic is independently testable and the network layer stays clean.

### Key design decisions

- `sessions` is `static` so all connections share one set, required for broadcasting to work
- `Collections.synchronizedSet()` makes the session set thread safe for concurrent connections
- All sends route through a single `sendTo()` helper with an `isOpen()` guard prevents crashes when a player disconnects mid broadcast
- Per player state lives in `session.getUserProperties()` a built in per connection key/value store

---

## How to run

**Prerequisites:** Java 17+, Maven

**1. Clone the repo**
```bash
git clone https://github.com/yourusername/number-guessing-game.git
cd number-guessing-game
```

**2. Build**
```bash
mvn compile
```

**3. Run the server** (in IntelliJ, hit ▶ Run on `Main.java`)

**4. Connect a player** (in terminal)
```bash
websocat ws://localhost:8025/game
```

Open multiple terminal windows to simulate multiple players racing each other.

> Install websocat via `brew install websocat` on Mac.

---

## How to play

```
Please enter a username to join the game
> Rodolfo
Welcome to Guess The Number!
> 50
Rodolfo: TOO HIGH
# of attempts: 1
> 25
Rodolfo: TOO LOW
# of attempts: 2
> 37
Rodolfo: YOU WIN
# of attempts: 3

STARTING NEW GAME — RANDOM NUMBER CHOSEN, START GUESSING!
```

---

## What I learned

- **The WebSocket protocol at the wire level** & the HTTP upgrade handshake, `101 Switching Protocols`, frame structure, opcodes, and why client → server frames are masked (I actually verified this myself with Wireshark)
- **JSR 356 lifecycle** — how `@OnOpen`, `@OnMessage`, `@OnClose`, and `@OnError` map directly onto the protocol events, and that the framework fires them in response to client actions, not you
- **Thread safety in real time servers** — why a plain `HashSet` crashes under concurrent connections and how `synchronizedSet` + a `synchronized` loop guards against it
- **Separation of concerns in network applications** — keeping WebSocket logic and game logic in separate classes so each has one clear responsibility
- **Per-connection state management** — using `getUserProperties()` to store per player data tied to each session's lifetime
