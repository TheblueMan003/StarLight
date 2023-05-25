scoreboard players add default.a.test.crashCount tbms.var 1
scoreboard players set default.a.test.crash._0 tbms.var 0
execute if score default.a.test.crashCount tbms.var matches 11.. run function default:zzz_sl_block/8
execute if score default.a.test.crash._0 tbms.var matches 0 run function default:a/test/run
