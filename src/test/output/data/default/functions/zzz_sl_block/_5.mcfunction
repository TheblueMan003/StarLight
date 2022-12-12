scoreboard players operation default.math.pow.parity tbms.var = default.math.pow.n tbms.var
scoreboard players operation default.math.pow.parity tbms.var %= 2 tbms.const
execute if score default.math.pow.parity tbms.var matches 1 run function default/zzz_sl_block/_4
scoreboard players operation default.math.pow.n tbms.var /= 2 tbms.const
scoreboard players operation default.math.pow.x tbms.var *= default.math.pow.x tbms.var
scoreboard players operation default.math.pow.x tbms.var /= 1000 tbms.const
function default:math/pow
scoreboard players operation default.math.pow._ret tbms.var = default.math.pow._ret tbms.var
