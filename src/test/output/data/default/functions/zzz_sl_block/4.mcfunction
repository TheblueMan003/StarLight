scoreboard players set default.test.main.0.innerLoop._0 tbms.var 0
execute unless block ~ ~ ~ minecraft:air run function default:zzz_sl_block/9
execute if score default.test.main.0.innerLoop.dist tbms.var matches ..0 if score default.test.main.0.innerLoop._0 tbms.var matches 0 run function default:zzz_sl_block/12
execute if score default.test.main.0.innerLoop._0 tbms.var matches 0 positioned ^ ^ ^0.1 run function default:zzz_sl_block/13
