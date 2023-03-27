scoreboard players add default.test.test.main.test.crashCount tbms.var 1
scoreboard players set default.test.test.main.test.crash._0 tbms.var 0
execute if score default.test.test.main.test.crashCount tbms.var matches 11.. run function default:zzz_sl_block/15
execute if score default.test.test.main.test.crash._0 tbms.var matches 0 run function default:zzz_sl_block/11
