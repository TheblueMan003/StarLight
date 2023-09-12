# ==================================================
# void default.zzz_sl_block.81()
# a.k.a default.math.pow.2
# ==================================================

scoreboard players set default.math.pow._0 tbms.var 1
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/80
scoreboard players operation default.math.pow._1._0 tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=__class__] if score default.math.pow._1._0 tbms.var = @s default.object.__ref run scoreboard players set @s default.standard.Exception.Exception.printMessage -1661209881
execute as @e[tag=__class__] if score default.__exceptionThrown tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players operation default.__exceptionThrown tbms.var = default.math.pow._1._0 tbms.var
execute as @e[tag=__class__] if score default.__exceptionThrown tbms.var = @s default.object.__ref run function default:object/__add-ref
