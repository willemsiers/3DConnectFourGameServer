# 3D Connect Four game.Game GameServer
Updated: 16-12-2016
Version: 0.3.1


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
    "event" : "lobby" | "game" | "started" | "make move" | "opponent moved" | "game over" | "error"
    "move" : "[a-d][1-4]"
    "winner" : "you" | "opponent" | "draw"
    "opponent" :  ([a-z][0-9])*
    "free lobbies" : [{
                        "room number" : [0-99]
                        "opponent"    : ([a-z][0-9])*
                        }]
    "reason" : "time elapsed" | "game full" | "missing keys" | "no such lobby" | "invalid move" | "lobby entry denied"
    "message" : any string explaining the message, for debugging purposes

}


```

The replies from the server do not happen synchronously every time, e.g. time error can occur anytime. This must be handled by the client correctly.
Just like the client messages, the server sends messages and not all name/value pairs are present every time. Below in the table is what a client can expect.

| Event | Message |
| ----- | -------- |
| Placed in the server.Lobby | event:lobby, free lobbies, message |
| Placed in a game | event:game, opponent, message|
| Game has started | event:started, message|
| Make move |  event:make move, message |
| Opponent moved | event:opponent moved, move, message |
| Game over | event:game over,winner,  message|
| Server Error | event:error, reason, message|
| Invalid Move | event:error, move, reason, message|


## GameServer properties

### Connecting

### server.Lobby

### game.Game
When a move is successful, the next message will be an "opponent moved" message or a "game over" message.

When a move is denied, two messages will be send. First an error message will be send. Then another "make move" message is send.


### Timeout

Timeouts happen in any state of a serverPlayer, below is the table for approximate timeouts for each state.

| State | Maximum | Action by server |
| ----- | ------- | ---------------- |
| server.Lobby | 600 s | Disconnected client |
| Game | 180 s | Put into the lobby |
| Make move | 15 s| Random move made for this client, opponents' turn |
| End of game | 180 s | Put into the lobby |


