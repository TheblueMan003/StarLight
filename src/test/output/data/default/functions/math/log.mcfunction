# ==================================================
# float default.math.log(float x)
# ==================================================
# ==================================================
# Compute the log of x with a taylor series
# ==================================================

scoreboard players set default.math.log.res tbms.var 0
scoreboard players set default.math.log.sign tbms.var 1000
scoreboard players set default.math.log.fact tbms.var 1000
scoreboard players set default.math.log.pow tbms.var 1000
scoreboard players set default.math.log._0.i tbms.var 0
execute if score default.math.log._0.i tbms.var matches ..9 run function default:zzz_sl_block/2
scoreboard players operation default.math.log._ret tbms.var = default.math.log.res tbms.var
