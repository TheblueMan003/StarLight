scoreboard players set default.math.raycast.0.innerLoop._0 tbms.var 0
execute if block ~ ~ ~ minecraft:stone run function default:zzz_sl_block/80
execute if score default.math.raycast.0.innerLoop.dist tbms.var matches ..0 if score default.math.raycast.0.innerLoop._0 tbms.var matches 0 run function default:zzz_sl_block/80
execute if score default.math.raycast.0.innerLoop._0 tbms.var matches 0 positioned ^ ^ ^1 run function default:zzz_sl_block/81
