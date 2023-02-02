package mc.java.armor_stand

def lazy setModel(mcobject $block, int $id){
    /data merge entity @s {ArmorItems: [{}, {}, {}, {id: "$block", Count: 1b, tag: {CustomModelData: $id}}],DisabledSlots:4144959}
}
