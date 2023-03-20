package test

import mc.inventory as inventory

public void main(){
    lazy val item2 = minecraft:iron_ingot
    if (inventory.isHoldingItem(item2)){
        /say hi
    }
}