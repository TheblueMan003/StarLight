



## teleportFront
- int $distance

## teleportHere


# Cutscene







## start
- entity e
Start the cutscene for the entities `e`
## init

Init the cutscene
## nextSegment

Start the next segment of the cutscene
## event
- void=>void fct
Call the function `fct` when this segment is reach and goes directly to the next
## waitFor
- int=>bool fct
Wait for the function `fct` to return true then goes to the next
## waitFor
- mcposition target
- int=>bool fct
Wait for the function `fct` to return true then goes to the next
## waitFor
- mcposition position
- mcposition target
- int=>bool fct
Wait for the function `fct` to return true then goes to the next
## waitTime
- int time
- int=>void fct
Wait for `time` ticks
## waitTime
- int time
- mcposition target
- int=>void fct
Wait for `time` ticks
## waitTime
- int time
- mcposition position
- mcposition target
- int=>void fct
Wait for `time` ticks
## pan
- float speed
- mcposition pos1
- mcposition pos2
- mcposition target
- int=>void whileActive
Move a fictive entity from `pos1` to `pos2`. The camera is located at `target` and look at the entity.
## pan
- float speed
- mcposition pos2
- mcposition target
- int=>void whileActive
Move a fictive entity from current camera pos to `pos2`. The camera is located at `target` and look at the entity.
## track
- float speed
- mcposition pos1
- mcposition pos2
- mcposition delta
- int=>void whileActive
Follow a fictive entity from `pos1` to `pos2` shifted by `delta`
## track
- float speed
- mcposition pos2
- mcposition delta
- int=>void whileActive
Follow a fictive entity from current camera pos to `pos2` shifted by `delta`
## linear
- float speed
- mcposition pos1
- mcposition pos2
- mcposition target
- int=>void whileActive
Move the camera from `pos1` to `pos2` while facing `target`
## linear
- float speed
- mcposition pos2
- mcposition target
- int=>void whileActive
Move the camera from current camera pos to `pos2` while facing `target`
## group
- void=>void fct
Group multiple instruction into one segment.
