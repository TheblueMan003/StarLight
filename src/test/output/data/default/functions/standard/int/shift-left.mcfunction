# ==================================================
# int default.standard.int.shiftLeft(int a, int b)
# ==================================================
# ==================================================
# Returns a shifted to the left by b bits.
# ==================================================

scoreboard players set default.standard.int.pow.x tbms.var 2
scoreboard players operation default.standard.int.pow.n tbms.var = default.standard.int.shiftLeft.b tbms.var
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.standard.int.shiftLeft.c tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.standard.int.shiftLeft._ret tbms.var = default.standard.int.shiftLeft.a tbms.var
scoreboard players operation default.standard.int.shiftLeft._ret tbms.var *= default.standard.int.shiftLeft.c tbms.var