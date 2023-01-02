package math.raycast

"""
Raycast with a `distance` and with step `precision`. Stop When `stopCondtion` is true or distance is reach.
Call `action` at the end with `true` if stopCondtion was `true` and false otherwise.
"""
def lazy shoot(float distance, float precision, bool stopCondtion, bool=>void action){
    float dis = distance
    def lazy shifted(float $d, void=>void fct){
        at(^ ^ ^$d)fct()
    }
    def innerLoop(float dist){
        if (stopCondtion){
            action(true)
        }
        else if (dist <= 0){
            action(false)
        }
        else{
            shifted(precision){
                innerLoop(dist - precision)
            }
        }
    }
    at(~ ~1.5 ~)innerLoop(dis)
}

"""
Raycast with a `distance` and with step `precision`. Stop When `stopCondtion` is true or distance is reach.
Call `step` at every step with argument corresponding to the distance remaining.
Call `action` at the end with `true` if stopCondtion was `true` and false otherwise.
"""
def lazy laser(float distance, float precision, bool stopCondtion, float=>void step, bool=>void action){
    float dis = distance
    def lazy shifted(float $d, void=>void fct){
        at(^ ^ ^$d)fct()
    }
    def innerLoop(float dist){
        step(dist)
        if (stopCondtion){
            action(true)
        }
        else if (dist <= 0){
            action(false)
        }
        else{
            shifted(precision){
                innerLoop(dist - precision)
            }
        }
    }
    at(~ ~1.5 ~)innerLoop(dis)
}