# ==================================================
# void default.zzz_sl_block.451()
# a.k.a default.math.pow.2
# ==================================================

scoreboard players set default.math.pow._0 tbms.var 1
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/466
scoreboard players operation default.math.pow._1._0 tbms.var = default.__totalRefCount tbms.var
execute as @e[tag=--class.default.standard.Exception.InvalidArgumentException] if score default.math.pow._1._0 tbms.var = @s default.object.__ref run scoreboard players set @s default.standard.Exception.Exception.printMessage 500520045
execute as @e[tag=--class.default.standard.Exception.Exception] if score default.__exceptionThrown tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players operation default.__exceptionThrown tbms.var = default.math.pow._1._0 tbms.var
execute as @e[tag=--class.default.standard.Exception.Exception] if score default.__exceptionThrown tbms.var = @s default.object.__ref if score @s default.object.__refCount matches 0.. run scoreboard players add @s default.object.__refCount 1
