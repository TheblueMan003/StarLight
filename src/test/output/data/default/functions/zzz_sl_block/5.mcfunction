# ==================================================
# void default.zzz_sl_block.5()
# a.k.a default.test.__init__
# ==================================================

execute as @e[tag=--class.default.test.C] if score default.test.c tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/0
scoreboard players operation default.test.c tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=--class.default.test.C] if score default.test.c tbms.var = @s default.object.__ref run function default:test/-c/__init__
