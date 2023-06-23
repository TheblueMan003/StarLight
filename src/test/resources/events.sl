package raycast

import mc.java.event as event
import test::Test
import standard::print

event.onInventoryChanged(){
    print("Inventory changed!")
}