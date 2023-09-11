package test

enum DamageType{
    Fire,
    Water
}

struct DamageData(DamageType type, int amount)

class A{
    int hp
    int[10] last
    def __init__(){

    }
    def damage(DamageData dmg){
        last[hp] = 1
        if (dmg.type == DamageType.Fire){
            hp -= dmg.amount * 2
        }
        hp -= dmg.amount
    }
}
class B extends A{

}

def test(){
    with(@e[type=pig]){
        /say there is a pig
    }
    else{
        /say there is no pig
    }
}

