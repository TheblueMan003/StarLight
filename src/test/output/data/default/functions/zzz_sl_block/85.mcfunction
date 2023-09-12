# ==================================================
# void default.zzz_sl_block.85()
# a.k.a default.math.pow.8
# ==================================================

scoreboard players operation default.math.pow._7.parity tbms.var = default.math.pow.n tbms.var
scoreboard players operation default.math.pow._7.parity tbms.var %= c2 tbms.const
execute if score default.math.pow._7.parity tbms.var matches 1 run function default:zzz_sl_block/84
scoreboard players operation default.math.pow.n tbms.var /= c2 tbms.const
scoreboard players operation default.math.pow.x tbms.var *= default.math.pow.x tbms.var
scoreboard players operation default.math.pow.x tbms.var /= c1000 tbms.const
scoreboard players operation default.math.pow.x tbms.var = default.math.pow.x tbms.var
scoreboard players operation default.math.pow.n tbms.var = default.math.pow.n tbms.var
scoreboard players operation default.math.pow.m tbms.var = default.math.pow.m tbms.var
function default:math/pow
scoreboard players operation default.math.pow._ret tbms.var = default.math.pow._ret tbms.var
