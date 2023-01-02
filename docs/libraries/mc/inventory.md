## private lazy void mc.inventory.setSlot(string $slot, mcobject $item, int $count)
- string $slot
- mcobject $item
- int $count



## private lazy void mc.inventory.setSlot(string $slot, int $slotid, mcobject $item, int $count, int $id)
- string $slot
- int $slotid
- mcobject $item
- int $count
- int $id



## lazy void mc.inventory.setMainHand(mcobject item, int count)
- mcobject item
- int count

Set the current entity's mainhand to `item` with `count`

## lazy void mc.inventory.setOffHand(mcobject item, int count)
- mcobject item
- int count

Set the current entity's offhand to `item` with `count`

## private lazy void mc.inventory.universalJavaSet(string slot, int slotID, mcobject item, int count)
- string slot
- int slotID
- mcobject item
- int count



## private lazy void mc.inventory.universalBedrockSet(string slot, int slotID, mcobject item, int count)
- string slot
- int slotID
- mcobject item
- int count



## lazy void mc.inventory.setHotbar(int slotID, mcobject item, int count)
- int slotID
- mcobject item
- int count

Set the current entity's hotbar slot `slotID` to `item` with `count`

## lazy void mc.inventory.setHotbar(mcobject item, int count)
- mcobject item
- int count

Set the current entity's whole hotbar to `item` with `count`

## lazy void mc.inventory.setHotbarRange(int min, int max, mcobject item, int count)
- int min
- int max
- mcobject item
- int count

Set the multiple slot int current entity's hotbar to `item` with `count`.Slot goes from `min` to `max` both included.

## lazy void mc.inventory.clearHotbar()
Clear the hotbar of the current entity

## lazy void mc.inventory.setInventorySlot(int index, mcobject item, int count)
- int index
- mcobject item
- int count

Set the inventory slot `index` of the current entity to `item` with `count`

## lazy void mc.inventory.setHelmet(mcobject item, int count)
- mcobject item
- int count

Set the current entity's helmet to `item` with `count`

## lazy void mc.inventory.setChestplate(mcobject item, int count)
- mcobject item
- int count

Set the current entity's chestplate to `item` with `count`

## lazy void mc.inventory.setLeggings(mcobject item, int count)
- mcobject item
- int count

Set the current entity's leggings to `item` with `count`

## lazy void mc.inventory.setBoots(mcobject item, int count)
- mcobject item
- int count

Set the current entity's boots to `item` with `count`

## predicate mc.inventory.isHoldingItem


## predicate mc.inventory.isHoldingItem


## predicate mc.inventory.isHoldingItem


## lazy bool mc.inventory.isHoldingItem(mcobject item, int count)
- mcobject item
- int count




