scoreboard players set default.test.abs.main._1 tbms.var 0
scoreboard players set default.math.abs.x tbms.var -5
function default:math/abs
execute unless score default.math.abs._ret tbms.var matches 0 run function default/zzz_sl_block/0
execute if score default.test.abs.main._1 tbms.var matches 0 run scoreboard players set default.test.abs.main._ret tbms.var 0
