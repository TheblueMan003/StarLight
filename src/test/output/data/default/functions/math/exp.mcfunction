# ==================================================
# float default.math.exp(float x)
# ==================================================
# ==================================================
# Compute the exp of x with a taylor series
# ==================================================

scoreboard players set default.math.exp.res tbms.var 0
scoreboard players set default.math.exp.sign tbms.var 1000
scoreboard players set default.math.exp.fact tbms.var 1000
scoreboard players set default.math.exp.pow tbms.var 1000
scoreboard players set default.math.exp._0.i tbms.var 0
execute if score default.math.exp._0.i tbms.var matches ..9 run function default:zzz_sl_block/1
scoreboard players operation default.math.exp._ret tbms.var = default.math.exp.res tbms.var
