# 3D Connect Four Game Server

## Usage

Connect via a standard TCP socket model. For Java client implementations, I recommend the standard Socket class.



## Protocol
The protocol uses JSON objects, any non-JSON object will be discarded.


For the client, JSON object should be in the following format.

```
{
    "action" : "connect" | "join" | "start" | "move" | "resign" | "restart" | "exit"
    "parameters" : [Move ("[a-d][1-4]"), Lobby number ([1-99])]
}

```

```

{
    "message" : "connected" | "joined" | "started"
    | "make move" | "move denied" |
    "parameters" : [Move ("[a-d][1-4]"), Free Lobbies ([1-99])]
}


```