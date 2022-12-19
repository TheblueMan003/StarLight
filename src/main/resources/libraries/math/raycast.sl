package math.raycast

def lazy shoot(float distance, float $precision, bool stopCondtion, bool=>void action){
    float dis = distance
    lazy float precision = $precision
    def innerLoop(float dist){
        if (stopCondtion){
            action(true)
        }
        else if (dist <= 0){
            action(false)
        }
        else{
            at(^ ^ ^$precision){
                innerLoop(dist - precision)
            }
        }
    }
    at(~ ~1.5 ~)innerLoop(dis)
}