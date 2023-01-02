## lazy void math.raycast.shoot(float distance, float precision, bool stopCondtion, bool=>void action)
- float distance
- float precision
- bool stopCondtion
- bool=>void action

Raycast with a `distance` and with step `precision`. Stop When `stopCondtion` is true or distance is reach.Call `action` at the end with `true` if stopCondtion was `true` and false otherwise.

## lazy void math.raycast.laser(float distance, float precision, bool stopCondtion, float=>void step, bool=>void action)
- float distance
- float precision
- bool stopCondtion
- float=>void step
- bool=>void action

Raycast with a `distance` and with step `precision`. Stop When `stopCondtion` is true or distance is reach.Call `step` at every step with argument corresponding to the distance remaining.Call `action` at the end with `true` if stopCondtion was `true` and false otherwise.


