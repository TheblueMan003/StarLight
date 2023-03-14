package mc.java.armor_stand

"""
Set the armor stand's model to a item with a custom model data
"""
def lazy setModel(mcobject $item, int $id){
    /data merge entity @s {ArmorItems: [{}, {}, {}, {id: "$item", Count: 1, tag: {CustomModelData: $id}}],DisabledSlots:4144959}
}


"""
Summon an armor stand's model with a item with a custom model data
"""
def lazy entity summon(mcobject $item, int $id){
    import cmd.entity as entity
    return entity.summon(minecraft:armor_stand, {Invisible:1,NoGravity:1,ArmorItems: [{}, {}, {}, {id: "$item", Count: 1, tag: {CustomModelData: $id}}],DisabledSlots:4144959})
}
