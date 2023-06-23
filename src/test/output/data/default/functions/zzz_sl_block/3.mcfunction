summon item ~ ~2 ~ {Tags:["newTag","slitted"],Item:{id:"minecraft:enchanted_book", Count:1, tag:{StoredEnchantments:[]}}}
data modify entity @e[tag=newTag,limit=1] Item.tag.StoredEnchantments append from entity @s Item.tag.StoredEnchantments[0]
execute as @e[tag=newTag,limit=1] run tag @s remove newTag
execute unless score default.main.main._0._0._0.c tbms.var = default.main.main._0._0._0.c tbms.var run scoreboard players set default.main.main._0._0._0.c tbms.var 0
execute store result score default.main.main._0._0._0.c tbms.var run CommandIR(kill @e[tag=slitted,type=item,nbt={Item:{id:"minecraft:enchanted_book", tag:{StoredEnchantments:[]}}}])
scoreboard players set default.main.main._0._0._0._2 tbms.var 0
execute if score default.main.main._0._0._0.c tbms.var matches 1.. run function default:zzz_sl_block/1
execute if score default.main.main._0._0._0._2 tbms.var matches 0 run scoreboard players add default.main.main._0._0.count tbms.var 1
