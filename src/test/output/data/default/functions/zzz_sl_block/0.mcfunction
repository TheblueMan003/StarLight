# ==================================================
# void default.zzz_sl_block.0()
# a.k.a default.object.0.2
# ==================================================

scoreboard players operation @s default.object.__ref = default.__totalRefCount tbms.var
scoreboard players set @s default.object.__refCount 1
tag @s remove cls_trg
tag @s add --class.default.test.A
scoreboard players set @s default.object.---__destroy__ 357358517
tag @s add --class.default.object
scoreboard players set @s default.test.A.get 0
