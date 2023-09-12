# ==================================================
# float default.math.cos(float angle)
# ==================================================
# ==================================================
# Compute the cossinus of the angle in degrees with a taylor series
# ==================================================

scoreboard players operation default.math.cos.angleRad tbms.var = default.math.cos.angle tbms.var
scoreboard players operation default.math.cos.angleRad tbms.var *= default.math.pi tbms.var
scoreboard players operation default.math.cos.angleRad tbms.var /= c1000 tbms.const
scoreboard players operation default.math.cos.angleRad tbms.var /= c180 tbms.const
scoreboard players set default.math.cos.res tbms.var 0
scoreboard players set default.math.cos.sign tbms.var 1000
scoreboard players set default.math.cos.fact tbms.var 1000
scoreboard players set default.math.cos.pow tbms.var 1000
scoreboard players set default.math.cos._0.i tbms.var 0
execute if score default.math.cos._0.i tbms.var matches ..9 run function default:zzz_sl_block/5
scoreboard players operation default.math.cos._ret tbms.var = default.math.cos.res tbms.var
