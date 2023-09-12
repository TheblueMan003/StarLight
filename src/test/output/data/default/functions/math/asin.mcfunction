# ==================================================
# float default.math.asin(float angle)
# ==================================================
# ==================================================
# Compute the arcsinus of the angle in degrees with a taylor series
# ==================================================

scoreboard players set default.math.asin.res tbms.var 0
scoreboard players set default.math.asin.sign tbms.var 1000
scoreboard players set default.math.asin.fact tbms.var 1000
scoreboard players set default.math.asin.pow tbms.var 1000
scoreboard players set default.math.asin._0.i tbms.var 0
execute if score default.math.asin._0.i tbms.var matches ..9 run function default:zzz_sl_block/4
scoreboard players operation default.math.asin._ret tbms.var = default.math.asin.res tbms.var
