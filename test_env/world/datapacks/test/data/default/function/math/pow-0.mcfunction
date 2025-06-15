# ==================================================
# int default.math.pow-0(int x, int n)
# a.k.a default.math.pow
# ==================================================
# ==================================================
# return x^n
# ==================================================

scoreboard players operation default.math.pow-0._ret tbms.var = default.math.pow-0.x tbms.var
scoreboard players operation default.standard.int.pow.x tbms.var = default.math.pow-0._ret tbms.var
scoreboard players operation default.standard.int.pow.n tbms.var = default.math.pow-0.n tbms.var
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.math.pow-0._ret tbms.var = default.standard.int.pow._ret tbms.var
