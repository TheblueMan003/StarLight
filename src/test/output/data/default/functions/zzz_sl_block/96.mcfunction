# ==================================================
# void default.zzz_sl_block.96()
# a.k.a default.object.3.2
# ==================================================

scoreboard players operation @s default.object.__ref = default.__totalRefCount tbms.var
scoreboard players set @s default.object.__refCount 1
tag @s remove cls_trg
tag @s add --class.default.standard.Exception.InvalidArgumentException
tag @s add --class.default.standard.Exception.Exception
scoreboard players set @s default.object.---__destroy__ 357358517
tag @s add --class.default.object
scoreboard players set @s default.standard.Exception.Exception.printStackTrace 0
scoreboard players set @s default.standard.Exception.Exception.printMessage 0
