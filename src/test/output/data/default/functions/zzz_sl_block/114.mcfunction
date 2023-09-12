# ==================================================
# void default.zzz_sl_block.114()
# a.k.a default.AnimatedEntity.__init__
# ==================================================

execute as @e[tag=__class__] if score default.AnimatedEntity.c tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/0
scoreboard players operation default.AnimatedEntity.c tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] if score default.AnimatedEntity.c tbms.var = @s default.object.__ref run function default:object/__init__
execute as @e[tag=__class__] if score default.AnimatedEntity.c tbms.var = @s default.object.__ref run function default:-animated-entity/-a/a
execute as @e[tag=__class__] if score default.AnimatedEntity.c tbms.var = @s default.object.__ref run function default:-animated-entity/-b/b
execute as @e[tag=__class__] if score default.AnimatedEntity.c tbms.var = @s default.object.__ref run function default:-animated-entity/-c/c
