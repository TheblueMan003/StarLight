# ==================================================
# int default.standard.int.pow(int x, int n, int m = 1)
# ==================================================
# ==================================================
# return x^n
# ==================================================

scoreboard players set default.standard.int.pow._0 tbms.var 0
execute if score default.standard.int.pow.n tbms.var matches 0 run function default:zzz_sl_block/10
execute if score default.standard.int.pow.n tbms.var matches 1 if score default.standard.int.pow._0 tbms.var matches 0 run function default:zzz_sl_block/11
execute if score default.standard.int.pow._0 tbms.var matches 0 run function default:zzz_sl_block/13
