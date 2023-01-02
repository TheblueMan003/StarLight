package mc.inventory

if (Compiler.isJava){
    def private lazy setSlot(string $slot, mcobject $item, int $count = 1){
        /item replace entity @s $slot with $item $count
    }
}
if (Compiler.isBedrock){
    def private lazy setSlot(string $slot, int $slotid, mcobject $item, int $count = 1, int $id = 0){
        /replaceitem entity @s $slot $slotid $item $count $id
    }
}

"""
Set the current entity's mainhand to `item` with `count`
"""
def lazy setMainHand(mcobject item, int count = 1){
    if (Compiler.isJava){
        setSlot("weapon.mainhand", item, count)
    }
    if (Compiler.isBedrock){
        lazy mcobject itemb = Compiler.getBedrockBlockName(item)
        lazy int itemd = Compiler.getBedrockBlockID(item)
        setSlot("slot.weapon.mainhand", 0, itemb, count, itemd)
    }
}

"""
Set the current entity's offhand to `item` with `count`
"""
def lazy setOffHand(mcobject item, int count = 1){
    if (Compiler.isJava){
        setSlot("weapon.offhand", item, count)
    }
    if (Compiler.isBedrock){
        lazy mcobject itemb = Compiler.getBedrockBlockName(item)
        lazy int itemd = Compiler.getBedrockBlockID(item)
        setSlot("slot.weapon.offhand", 0, itemb, count, itemd)
    }
}

def private lazy universalJavaSet(string slot, int slotID, mcobject item, int count = 1){
        lazy string nslot = slot + "." + slotID
        setSlot(nslot, item, count)
}

def private lazy universalBedrockSet(string slot, int slotID, mcobject item, int count = 1){
    lazy mcobject itemb = Compiler.getBedrockBlockName(item)
    lazy int itemd = Compiler.getBedrockBlockID(item)
    setSlot(slot, slotID, itemb, count, itemd)
}

"""
Set the current entity's hotbar slot `slotID` to `item` with `count`
"""
def lazy setHotbar(int slotID, mcobject item, int count = 1){
    if (Compiler.isJava){
        universalJavaSet("hotbar", slotID, item, count)
    }
    if (Compiler.isBedrock){
        universalBedrockSet("slot.hotbar", slotID, item, count)
    }
}

"""
Set the current entity's whole hotbar to `item` with `count`
"""
def lazy setHotbar(mcobject item, int count = 1){
    if (Compiler.isJava){
        foreach(slotID in 0..8){
            universalJavaSet("hotbar", slotID, item, count)
        }
    }
    if (Compiler.isBedrock){
        foreach(slotID in 0..8){
            universalBedrockSet("slot.hotbar", slotID, item, count)
        }
    }
}

"""
Set the multiple slot int current entity's hotbar to `item` with `count`.
Slot goes from `min` to `max` both included.
"""
def lazy setHotbarRange(int min, int max, mcobject item, int count = 1){
    if (Compiler.isJava){
        foreach(slotID in min..max){
            universalJavaSet("hotbar", slotID, item, count)
        }
    }
    if (Compiler.isBedrock){
        foreach(slotID in min..max){
            universalBedrockSet("slot.hotbar", slotID, item, count)
        }
    }
}

"""
Clear the hotbar of the current entity
"""
def lazy clearHotbar(){
    setHotbar(minecraft:air, 1)
}

"""
Set the inventory slot `index` of the current entity to `item` with `count`
"""
def lazy setInventorySlot(int index, mcobject item, int count = 1){
    if (Compiler.isJava){
        universalJavaSet("inventory", slotID, item, count)
    }
    if (Compiler.isBedrock){
        universalBedrockSet("slot.inventory", slotID, item, count)
    }
}

"""
Set the current entity's helmet to `item` with `count`
"""
def lazy setHelmet(mcobject item, int count = 1){
    if (Compiler.isJava){
        setSlot("armor.head", item, count)
    }
    if (Compiler.isBedrock){
        universalBedrockSet("slot.armor.head", 0, item, count)
    }
}

"""
Set the current entity's chestplate to `item` with `count`
"""
def lazy setChestplate(mcobject item, int count = 1){
    if (Compiler.isJava){
        setSlot("armor.chest", item, count)
    }
    if (Compiler.isBedrock){
        universalBedrockSet("slot.armor.chest", 0, item, count)
    }
}

"""
Set the current entity's leggings to `item` with `count`
"""
def lazy setLeggings(mcobject item, int count = 1){
    if (Compiler.isJava){
        setSlot("armor.legs", item, count)
    }
    if (Compiler.isBedrock){
        universalBedrockSet("slot.armor.legs", 0, item, count)
    }
}

"""
Set the current entity's boots to `item` with `count`
"""
def lazy setBoots(mcobject item, int count = 1){
    if (Compiler.isJava){
        setSlot("armor.feet", item, count)
    }
    if (Compiler.isBedrock){
        universalBedrockSet("slot.armor.feet", 0, item, count)
    }
}



predicate isHoldingItem(mcobject item, int count, string data){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "equipment": {
            "mainhand": {
                "items":[ item ],
                "count": count,
                "nbt": data
            }
        }
    }
}

predicate isHoldingItem(mcobject item, int count){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "equipment": {
            "mainhand": {
                "items":[ item ],
                "count": count
            }
        }
    }
}

predicate isHoldingItem(mcobject item){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "equipment": {
            "mainhand": {
                "items":[ item ]
            }
        }
    }
}


if (Compiler.isBedrock){
    def lazy bool isHoldingItem(mcobject item, int count = 1){
        return @s[hasitem={item=item,quantity=count,location=slot.weapon.mainhand,slot=0}]
    }
}