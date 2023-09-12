# ==================================================
# float default.math.sqrt2(float value)
# ==================================================
# ==================================================
# Compute the square root of the value with a taylor series
# ==================================================

scoreboard players set default.math.sqrt2.res tbms.var 0
scoreboard players set default.math.sqrt2.sign tbms.var 1000
scoreboard players set default.math.sqrt2.fact tbms.var 1000
scoreboard players set default.math.sqrt2.pow tbms.var 1000
scoreboard players set default.math.sqrt2._0.i tbms.var 0
execute if score default.math.sqrt2._0.i tbms.var matches ..9 run function default:zzz_sl_block/3
scoreboard players operation default.math.sqrt2._ret tbms.var = default.math.sqrt2.res tbms.var
