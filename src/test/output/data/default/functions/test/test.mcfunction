scoreboard players set default.test.test._0 tbms.var 1
execute as @e[type=pig] run function default:zzz_sl_block/0
execute unless score default.test.test._0 tbms.var matches 0 run say there is no pig
