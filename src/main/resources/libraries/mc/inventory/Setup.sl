package mc.inventory.Setup

private lazy val lock = {"item_lock": {"mode": "lock_in_slot"}}

template Setup{
    import mc.java.event as event
    import mc.inventory as inventory

    entity applyTo

    """
    Set the current entity's mainhand to `item` with `count`
    """
    def lazy forceMainHand(mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setMainHand(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setMainHand(item, count, lock)
            }
        }
    }

    """
    Set the current entity's offhand to `item` with `count`
    """
    def lazy forceOffHand(mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setOffHand(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setOffHand(item, count, lock)
            }
        }
    }

    """
    Set the current entity's hotbar slot `slotID` to `item` with `count`
    """
    def lazy forceHotbar(int slotID, mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setHotbar(slotID, item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setHotbar(slotID, item, count, lock)
            }
        }
    }

    """
    Set the current entity's whole hotbar to `item` with `count`
    """
    def lazy forceHotbar(mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setHotbar(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setHotbar(item, count, lock)
            }
        }
    }

    """
    Set the multiple slot int current entity's hotbar to `item` with `count`.
    Slot goes from `min` to `max` both included.
    """
    def lazy forceHotbarRange(int min, int max, mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setHotbarRange(min, max, item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setHotbarRange(min, max, item, count, lock)
            }
        }
    }

    """
    Clear the hotbar of the current entity
    """
    def lazy clearHotbar(){
    }

    """
    Set the inventory slot `index` of the current entity to `item` with `count`
    """
    def lazy forceInventorySlot(int index, mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setInventorySlot(index, item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setInventorySlot(index, item, count, lock)
            }
        }
    }

    """
    Set the current entity's helmet to `item` with `count`
    """
    def lazy forceHelmet(mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setHelmet(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setHelmet(item, count, lock)
            }
        }
    }

    """
    Set the current entity's chestplate to `item` with `count`
    """
    def lazy forceChestplate(mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setChestplate(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setChestplate(item, count, lock)
            }
        }
    }

    """
    Set the current entity's leggings to `item` with `count`
    """
    def lazy forceLeggings(mcobject item, int count = 1){
        def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setLeggings(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setLeggings(item, count, lock)
            }
        }
    }

    """
    Set the current entity's boots to `item` with `count`
    """
    def lazy forceBoots(mcobject item, int count = 1){
       def @templates.reload set(){
            if (Compiler.isJava()){
                inventory.setBoots(item, count)
            }
            if (Compiler.isBedrock()){
                inventory.setBoots(item, count, lock)
            }
        }
    }


    if (Compiler.isJava()){
        def [Compile.order=9999] handler(){
            event.onInventoryChanged(){
                if (@s in applyTo && @s[gamemode=!creative]){
                    inventory.clear()
                    @templates.reload()
                }
            }
        }
    }

    def add(){
        if (!(@s in applyTo)){
            applyTo += @s
            inventory.clear()
            @templates.reload()
        }
    }
    def lazy add(entity e){
        with(e){
            add()
        }
    }
    def lazy remove(){
        applyTo -= @s
    }
    def lazy remove(entity e){
        applyTo -= e
    }
}