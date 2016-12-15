# 3D Connect Four Game Server

## Usage

Connect via a standard TCP socket model. For Java client implementations, I recommend the standard Socket class.



## Protocol
The protocol uses JSON objects, any non-JSON object will be discarded.


For the client, JSON object should be in the following format.

```
{
    "action" : "connect" | "join" | "start" | "move" | "resign" | "restart" | "exit"
    "move" : [a-d][1-4] | null
    "lobby number" : [1-99] | null
    "name" : ([a-z][0-9])*
}

```

```

{
    "message" : "enter lobby" | "game" | "started" | "make move" | "move denied" | "you won" | "you lose"
    "parameters" : [Move ("[a-d][1-4]"), Free Lobbies ([{"room number": [0-99] "opponent" : ([a-z][0-9])* ]), "timeout"]
}


```

## Server properties

 ### Connecting

 ### Lobby

 ### Game

 ### Timeout