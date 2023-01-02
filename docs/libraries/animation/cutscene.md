## private lazy void animation.cutscene.teleportFront(int $distance)
- int $distance



## private lazy void animation.cutscene.teleportHere()


# template animation.cutscene.Cutscene


## lazy void animation.cutscene.start(entity e)
- entity e

Start the cutscene for the entities `e`

## void animation.cutscene.init()
Init the cutscene

## void animation.cutscene.nextSegment()
Start the next segment of the cutscene

## lazy void animation.cutscene.event(void=>void fct)
- void=>void fct

Call the function `fct` when this segment is reach and goes directly to the next

## lazy void animation.cutscene.waitFor(int=>bool fct)
- int=>bool fct

Wait for the function `fct` to return true then goes to the next

## lazy void animation.cutscene.waitFor(mcposition target, int=>bool fct)
- mcposition target
- int=>bool fct

Wait for the function `fct` to return true then goes to the next

## lazy void animation.cutscene.waitFor(mcposition position, mcposition target, int=>bool fct)
- mcposition position
- mcposition target
- int=>bool fct

Wait for the function `fct` to return true then goes to the next

## lazy void animation.cutscene.waitTime(int time, int=>void fct)
- int time
- int=>void fct

Wait for `time` ticks

## lazy void animation.cutscene.waitTime(int time, mcposition target, int=>void fct)
- int time
- mcposition target
- int=>void fct

Wait for `time` ticks

## lazy void animation.cutscene.waitTime(int time, mcposition position, mcposition target, int=>void fct)
- int time
- mcposition position
- mcposition target
- int=>void fct

Wait for `time` ticks

## lazy void animation.cutscene.pan(float speed, mcposition pos1, mcposition pos2, mcposition target, int=>void whileActive)
- float speed
- mcposition pos1
- mcposition pos2
- mcposition target
- int=>void whileActive

Move a fictive entity from `pos1` to `pos2`. The camera is located at `target` and look at the entity.

## lazy void animation.cutscene.pan(float speed, mcposition pos2, mcposition target, int=>void whileActive)
- float speed
- mcposition pos2
- mcposition target
- int=>void whileActive

Move a fictive entity from current camera pos to `pos2`. The camera is located at `target` and look at the entity.

## lazy void animation.cutscene.track(float speed, mcposition pos1, mcposition pos2, mcposition delta, int=>void whileActive)
- float speed
- mcposition pos1
- mcposition pos2
- mcposition delta
- int=>void whileActive

Follow a fictive entity from `pos1` to `pos2` shifted by `delta`

## lazy void animation.cutscene.track(float speed, mcposition pos2, mcposition delta, int=>void whileActive)
- float speed
- mcposition pos2
- mcposition delta
- int=>void whileActive

Follow a fictive entity from current camera pos to `pos2` shifted by `delta`

## lazy void animation.cutscene.linear(float speed, mcposition pos1, mcposition pos2, mcposition target, int=>void whileActive)
- float speed
- mcposition pos1
- mcposition pos2
- mcposition target
- int=>void whileActive

Move the camera from `pos1` to `pos2` while facing `target`

## lazy void animation.cutscene.linear(float speed, mcposition pos2, mcposition target, int=>void whileActive)
- float speed
- mcposition pos2
- mcposition target
- int=>void whileActive

Move the camera from current camera pos to `pos2` while facing `target`

## lazy void animation.cutscene.group(void=>void fct)
- void=>void fct

Group multiple instruction into one segment.




