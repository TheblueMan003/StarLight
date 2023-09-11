scoreboard players set default.test.test._2 tbms.var 0
scoreboard players set default.test.test._1 tbms.var 0
execute as @a at @s unless score @s default.test.a matches 0 run scoreboard players set default.test.test._1 tbms.var 1
execute unless score default.test.test._1 tbms.var matches 0 run function default:zzz_sl_block/0
execute if score default.test.test._2 tbms.var matches 0 run scoreboard players set default.test.test._0 tbms.var 0
scoreboard players operation default.test.test.test tbms.var = default.test.test._0 tbms.var
execute unless score default.test.test.test tbms.var matches 0 run say hi
scoreboard players set default.test.test._5.i tbms.var 0
execute if score default.test.test._5.i tbms.var matches ..-1 run function default:zzz_sl_block/1
