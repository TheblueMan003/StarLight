# ==================================================
# int default.math.factorial(int x)
# ==================================================
# ==================================================
# return x!
# ==================================================

scoreboard players set default.math.factorial.__hasFunctionReturned__ tbms.var 0
execute if score default.math.factorial.x tbms.var matches ..-1 run function default:zzz_sl_block/87
execute if score default.math.factorial.__hasFunctionReturned__ tbms.var matches 0 run function default:zzz_sl_block/89
