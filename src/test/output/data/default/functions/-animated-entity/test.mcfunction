# ==================================================
# void default.AnimatedEntity.test()
# ==================================================

scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:cow ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/0
scoreboard players operation default.AnimatedEntity.test._0 tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] if score default.AnimatedEntity.test._0 tbms.var = @s default.object.__ref run function default:object/__init__
