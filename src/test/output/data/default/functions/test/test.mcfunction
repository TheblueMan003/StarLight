# ==================================================
# void default.test.test()
# ==================================================

execute as @e[tag=--class.default.test.A] if score default.test.test.a tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/0
scoreboard players operation default.test.test.a tbms.var = default.__totalRefCount tbms.var
