execute as @e[tag=__class__] at @s if score default.test.test tbms.var = @s s1644059425 run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/1
scoreboard players operation default.test.test tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] at @s if score default.test.test tbms.var = @s s1644059425 run function default:zzz_sl_block/2
