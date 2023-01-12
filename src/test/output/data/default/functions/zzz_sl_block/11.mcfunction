execute as @e[tag=__class__] at @s if score default.standard.array.Array--1633011157.impl.__init__._0 tbms.var = @s s1644059425 run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/9
scoreboard players operation @s s2003199141 = default.__totalRefCount tbms.var
execute as @e[tag=__class__] at @s if score default.standard.array.Array--1633011157.impl.__init__._2 tbms.var = @s s1644059425 run function default:zzz_sl_block/10
