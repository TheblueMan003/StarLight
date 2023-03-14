package mc.inventory

if (Compiler.isJava){
    def private lazy setSlot(string $slot, mcobject $item, int $count = 1){
        /item replace entity @s $slot with $item $count
    }
}
if (Compiler.isBedrock){
    def private lazy setSlot(string $slot, int $slotid, mcobject $item, int $count = 1, int $id = 0, json $nbt = {}){
        /replaceitem entity @s $slot $slotid $item $count $id $nbt
    }
}

if (Compiler.isJava){
    """
    Set the current entity's mainhand to `item` with `count`
    """
    def lazy setMainHand(mcobject item, int count = 1){
        setSlot("weapon.mainhand", item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's mainhand to `item` with `count`
    """
    def lazy setMainHand(mcobject item, int count = 1, json nbt = {}){
        lazy mcobject itemb = Compiler.getBedrockBlockName(item)
        lazy int itemd = Compiler.getBedrockBlockID(item)
        setSlot("slot.weapon.mainhand", 0, itemb, count, itemd, nbt)
    }
}

if (Compiler.isJava){
    """
    Set the current entity's offhand to `item` with `count`
    """
    def lazy setOffHand(mcobject item, int count = 1){
        setSlot("weapon.offhand", item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's offhand to `item` with `count`
    """
    def lazy setOffHand(mcobject item, int count = 1, json nbt = {}){
        lazy mcobject itemb = Compiler.getBedrockBlockName(item)
        lazy int itemd = Compiler.getBedrockBlockID(item)
        setSlot("slot.weapon.offhand", 0, itemb, count, itemd, nbt)
    }
}

def private lazy universalJavaSet(string slot, int slotID, mcobject item, int count = 1){
    lazy string nslot = slot + "." + slotID
    setSlot(nslot, item, count)
}

def private lazy universalBedrockSet(string slot, int slotID, mcobject item, int count = 1, json nbt = {}){
    lazy mcobject itemb = Compiler.getBedrockBlockName(item)
    lazy int itemd = Compiler.getBedrockBlockID(item)
    setSlot(slot, slotID, itemb, count, itemd, nbt)
}

if (Compiler.isJava){
    """
    Set the current entity's hotbar slot `slotID` to `item` with `count`
    """
    def lazy setHotbar(int slotID, mcobject item, int count = 1){
        universalJavaSet("hotbar", slotID, item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's hotbar slot `slotID` to `item` with `count`
    """
    def lazy setHotbar(int slotID, mcobject item, int count = 1, json nbt = {}){
        universalBedrockSet("slot.hotbar", slotID, item, count, nbt)
    }
}

if (Compiler.isJava){
    """
    Set the current entity's whole hotbar to `item` with `count`
    """
    def lazy setHotbar(mcobject item, int count = 1){
        foreach(slotID in 0..8){
            universalJavaSet("hotbar", slotID, item, count)
        }
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's whole hotbar to `item` with `count`
    """
    def lazy setHotbar(mcobject item, int count = 1, json nbt = {}){
        foreach(slotID in 0..8){
            universalBedrockSet("slot.hotbar", slotID, item, count, nbt)
        }
    }
}

if (Compiler.isJava){
    """
    Set the multiple slot int current entity's hotbar to `item` with `count`.
    Slot goes from `min` to `max` both included.
    """
    def lazy setHotbarRange(int min, int max, mcobject item, int count = 1){
        foreach(slotID in min..max){
            universalJavaSet("hotbar", slotID, item, count)
        }
    }
}
if (Compiler.isBedrock){
    """
    Set the multiple slot int current entity's hotbar to `item` with `count`.
    Slot goes from `min` to `max` both included.
    """
    def lazy setHotbarRange(int min, int max, mcobject item, int count = 1, json nbt = {}){
        foreach(slotID in min..max){
            universalBedrockSet("slot.hotbar", slotID, item, count, nbt)
        }
    }
}

"""
Clear the hotbar of the current entity
"""
def lazy clearHotbar(){
    setHotbar(minecraft:air, 1)
}

if (Compiler.isJava){
    """
    Set the inventory slot `index` of the current entity to `item` with `count`
    """
    def lazy setInventorySlot(int index, mcobject item, int count = 1){
        universalJavaSet("inventory", slotID, item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the inventory slot `index` of the current entity to `item` with `count`
    """
    def lazy setInventorySlot(int index, mcobject item, int count = 1, json nbt = {}){
        universalBedrockSet("slot.inventory", slotID, item, count, nbt)
    }
}


if (Compiler.isJava){
    """
    Set the current entity's helmet to `item` with `count`
    """
    def lazy setHelmet(mcobject item, int count = 1){
        setSlot("armor.head", item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's helmet to `item` with `count`
    """
    def lazy setHelmet(mcobject item, int count = 1, json nbt = {}){
        universalBedrockSet("slot.armor.head", 0, item, count, nbt)
    }
}


if (Compiler.isJava){
    """
    Set the current entity's chestplate to `item` with `count`
    """
    def lazy setChestplate(mcobject item, int count = 1){
        setSlot("armor.chest", item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's chestplate to `item` with `count`
    """
    def lazy setChestplate(mcobject item, int count = 1, json nbt = {}){
        universalBedrockSet("slot.armor.chest", 0, item, count, nbt)
    }
}

if (Compiler.isJava){
    """
    Set the current entity's leggings to `item` with `count`
    """
    def lazy setLeggings(mcobject item, int count = 1){
        setSlot("armor.legs", item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's leggings to `item` with `count`
    """
    def lazy setLeggings(mcobject item, int count = 1, json nbt = {}){
        universalBedrockSet("slot.armor.legs", 0, item, count, nbt)
    }
}


if (Compiler.isJava){
    """
    Set the current entity's boots to `item` with `count`
    """
    def lazy setBoots(mcobject item, int count = 1){
        setSlot("armor.feet", item, count)
    }
}
if (Compiler.isBedrock){
    """
    Set the current entity's boots to `item` with `count`
    """
    def lazy setBoots(mcobject item, int count = 1, json nbt = {}){
        universalBedrockSet("slot.armor.feet", 0, item, count, nbt)
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

"""
Clear the current entity's inventory
"""
def lazy clear(){
    /clear @s
}

"""
Clear the current entity's inventory of `item`
"""
def lazy clear(mcobject $item){
    /clear @s $item
}

"""
Clear the current entity's inventory of `item` with `count`
"""
def lazy clear(mcobject $item, int $count){
    /clear @s $item $count
}

"""
Clear the current entity's inventory and return the number of items cleared
"""
[noReturnCheck=true] lazy int clearCount(){
    if (Compiler.isJava()){
        Compiler.cmdstore(_ret){
            clear()
        }
    }
    if (Compiler.isBedrock()){
        int count = 0
        while(@s[hasitem={quantity=1..}]){
            clear(item, 1)
            count += 1
        }
        return count
    }
}

"""
Clear the current entity's inventory of `item` and return the number of items cleared
"""
[noReturnCheck=true] lazy int clearCount(mcobject item){
    if (Compiler.isJava()){
        Compiler.cmdstore(_ret){
            clear(item)
        }
    }
    if (Compiler.isBedrock()){
        int count = 0
        while(@s[hasitem={item=item,quantity=1..}]){
            clear(item, 1)
            count += 1
        }
        return count
    }
}

"""
Clear the current entity's inventory of `item` with a max of `count` and return the number of items cleared
"""
[noReturnCheck=true] lazy int clearCount(mcobject item, int count){
    if (Compiler.isJava()){
        Compiler.cmdstore(_ret){
            clear(item, count)
        }
    }
    if (Compiler.isBedrock()){
        int c = 0
        while(@s[hasitem={item=item,quantity=1..}] && c < count){
            clear(item, 1)
            c += 1
        }
        return c
    }
}