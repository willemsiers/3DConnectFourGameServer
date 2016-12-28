# 3D Connect Four Game Server
Updated: 16-12-2016
Version: 0.1


## Usage

Connect via a standard TCP socket model. For Java client implementations, I recommend the standard Socket class.
The IP address and Port number will be given when the tournament starts.

IMPORTANT: Always end a message with a new line "\n"

## Protocol
The protocol uses JSON objects, any non-JSON object will be discarded.


For the client, JSON object should be in the following format.

```
{
    "action" : "connect" | "join" | "start" | "move" | "resign" | "restart" | "exit game" | "disconnect"
    "move" : [a-d][1-4]
    "room number" : [0-99]
    "name" : ([a-z][0-9])*
}

```

Some name/value pairs are not required in every message. The table below shows what is needed when.

| Action        | Required                  |
| ------        | --------                  |
| Connect       | action:connect, name      |
| Join game     | action:join, room number |
| Start game    | action:start              |
| Move          | action:move, move         |
| Resign        | action:resign             |
| Restart       | action:restart            |
| Exit game     | action:exit game          |
| Disconnect    | action:disconnect        |



```

{
    "event" : "lobby" | "game" | "started" | "make move" | "move denied" | "opponent moved" | "won" | "lost" | "error"
    "move" : "[a-d][1-4]"
    "opponent" :  ([a-z][0-9])*
    "free lobbies" : [{
                        "room number" : [0-99]
                        "opponent"    : ([a-z][0-9])*
                        }]
    "first player" : true | false
    "reason" : "time elapsed" | "game full" | "missing keys" | "no such lobby" | "invalid move" | "lobby entry denied"
    "message" : any string explaining the message, for debugging purposes

}


```

The replies from the server do not happen synchronously every time, e.g. time error can occur anytime. This must be handled by the client correctly.
Just like the client messages, the server sends messages and not all name/value pairs are present every time. Below in the table is what a client can expect.

| Event | Message |
| ----- | -------- |
| Placed in the Lobby | event:lobby, free lobbies, message |
| Placed in a game | event:game, opponent, message|
| Game has started | event:started, first player, message|
| Make move |  event:make move, message |
| Move denied | event:move denied, move,  message|
| Opponent moved | event:opponent moved, move, message |
| You won the game | event:won, message|
| You lost the game | event:lost, message |
| Server Error | event:error, reason, message|


## Server properties

### Connecting

### Lobby

### Game

### Timeout

TTimeouts happen in any state of a player, below is the table for approximate timeouts for each state.

| State | Maximum | Action by server |
| ----- | ------- | ---------------- |
| Lobby | 300 s | Disconnected client |
| Game | 60 s | Put into the lobby |
| Make move | 5 s| Random move made for this client, opponents' turn |
| End of game | 60 s | Put into the lobby |


