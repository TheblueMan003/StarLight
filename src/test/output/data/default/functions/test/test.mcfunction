execute as @e[tag=__class__] if score default.test.test.a tbms.var = @s default.object.__ref run function default:object/__rem-ref
scoreboard players add default.__totalRefCount tbms.var 1
summon minecraft:marker ~ ~ ~ {Tags:["__class__","cls_trg"]}
execute as @e[tag=cls_trg] run function default:zzz_sl_block/1
scoreboard players operation default.test.test.a tbms.var = default.__totalRefCount tbms.var
scoreboard players set default.test.test.dmg.__init__.type tbms.var 0
scoreboard players set default.test.test.dmg.__init__.amount tbms.var 10
scoreboard players operation default.test.test.dmg.type tbms.var = default.test.test.dmg.__init__.type tbms.var
scoreboard players operation default.test.test.dmg.amount tbms.var = default.test.test.dmg.__init__.amount tbms.var
execute as @e[tag=__class__] if score default.test.test.a tbms.var = @s default.object.__ref run function default:zzz_sl_block/3
