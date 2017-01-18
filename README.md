# 3D Connect Four game.Game GameServer
Updated: 16-12-2016
Version: 0.4


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
    "winning move" : [("[0-3][0-3][0-3]")^4]
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
| Placed in the Lobby    | event:lobby, free lobbies, message |
| Placed in a game              | event:game, opponent, message|
| Game has started              | event:started, opponent, message|
| Make move                     | event:make move, message |
| Opponent moved                | event:opponent moved, move, message |
| Game over                     | event:game over, winner, winning move, message|
| Server Error                  | event:error, reason, message|
| Invalid Move                  | event:error, move, reason, message|

### Flow

| State | Possible messages|
| -------| --------------|
| Disconnected      | connect |
| Lobby             | connect, join, disconnect|
| Game              | start, exit game , disconnect |
| Make move         | move, resign, exit game, disconnect |
| Game over         | restart, exit game, disconnect |






## GameServer properties

### Connecting
Rules for the name:
- "No player1 yet" is forbidden
- Max 16 characters
- Any character other than [A-Z][a-z][0-9] is forbidden.

Players who not stick to the rules will not be allowed access onto the server.


### Lobby

It could happen that when joining a certain game, the chosen game contains already the maximum amount of players. Then the server sends an error message containing "game full".

### Game
Concerning moves:
- [a-d] is for the y-axis.
- [0-3] is for the x-axis.

When a move is successful, the next message will be an "opponent moved" message or a "game over" message.

When a move is denied, two messages will be send. First an error message will be send. Then another "make move" message is send.

The game over message contains a winning move array to see where you or the opponent have placed the winning move.

After the game over state, when one player wants to restart and the other does not, the game is reset and both players will be placed in the lobby.

### Timeout

Timeouts happen in any state of a serverPlayer, below is the table for approximate timeouts for each state.

| State | Maximum | Action by server |
| ----- | ------- | ---------------- |
| Lobby | 600 s | Disconnected client |
| Game | 180 s | Put into the lobby |
| Make move | 15 s| Random move made for this client, opponents' turn |
| End of game | 180 s | Put into the lobby |


