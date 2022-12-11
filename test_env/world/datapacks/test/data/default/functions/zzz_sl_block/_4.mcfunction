scoreboard players operation default.math.pow-0.parity tbms.var = default.math.pow-0.x tbms.var
scoreboard players operation default.math.pow-0.parity tbms.var %= 2 tbms.const
execute if score default.math.pow-0.parity tbms.var matches 1 run function default/zzz_sl_block/_3
scoreboard players operation default.math.pow-0.n tbms.var /= 2 tbms.const
scoreboard players operation default.math.pow-0.x tbms.var *= default.math.pow-0.x tbms.var
function default:math/pow-0
scoreboard players operation default.math.pow-0._ret tbms.var = default.math.pow-0._ret tbms.var
