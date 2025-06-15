# ==================================================
# float default.math.pow(float x, int n, float m = 1)
# ==================================================
# ==================================================
# return x^n
# ==================================================

scoreboard players set default.math.pow._0 tbms.var 0
execute if score default.math.pow.n tbms.var matches ..-1 run function default:zzz_sl_block/451
execute if score default.math.pow.n tbms.var matches 0 if score default.math.pow._0 tbms.var matches 0 run function default:zzz_sl_block/452
execute if score default.math.pow.n tbms.var matches 1 if score default.math.pow._0 tbms.var matches 0 run function default:zzz_sl_block/453
execute if score default.math.pow._0 tbms.var matches 0 run function default:zzz_sl_block/455
