# ==================================================
# void default.zzz_sl_block.97()
# a.k.a default.math.sqrt._0.1
# ==================================================

scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/96
scoreboard players operation default.math.sqrt._0._0._0 tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] if score default.math.sqrt._0._0._0 tbms.var = @s default.object.__ref run scoreboard players set @s default.standard.Exception.Exception.printMessage 634994718
execute as @e[tag=__class__] if score default.__exceptionThrown tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players operation default.__exceptionThrown tbms.var = default.math.sqrt._0._0._0 tbms.var
execute as @e[tag=__class__] if score default.__exceptionThrown tbms.var = @s default.object.__ref run function default:object/__add-ref
scoreboard players set default.math.sqrt.__hasFunctionReturned__ tbms.var 2
