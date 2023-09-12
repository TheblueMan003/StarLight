# ==================================================
# int default.standard.int.shiftRight(int a, int b)
# ==================================================
# ==================================================
# Returns a shifted to the right by b bits.
# ==================================================

scoreboard players set default.standard.int.pow.x tbms.var 2
scoreboard players operation default.standard.int.pow.n tbms.var = default.standard.int.shiftRight.b tbms.var
scoreboard players set default.standard.int.pow.m tbms.var 1
function default:standard/int/pow
scoreboard players operation default.standard.int.shiftRight.c tbms.var = default.standard.int.pow._ret tbms.var
scoreboard players operation default.standard.int.shiftRight._ret tbms.var = default.standard.int.shiftRight.a tbms.var
scoreboard players operation default.standard.int.shiftRight._ret tbms.var /= default.standard.int.shiftRight.c tbms.var
