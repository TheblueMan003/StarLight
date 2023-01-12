scoreboard players add default.a.adas.crashCount tbms.var 1
scoreboard players set default.a.adas.crash._0 tbms.var 0
execute if score default.a.adas.crashCount tbms.var matches 11.. run function default:zzz_sl_block/5
execute if score default.a.adas.crash._0 tbms.var matches 0 run function default:a/adas/run
