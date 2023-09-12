# ==================================================
# float default.math.sin(float angle)
# ==================================================
# ==================================================
# Compute the sinus of the angle in degrees with a taylor series
# ==================================================

scoreboard players operation default.math.sin.angleRad tbms.var = default.math.sin.angle tbms.var
scoreboard players operation default.math.sin.angleRad tbms.var *= default.math.pi tbms.var
scoreboard players operation default.math.sin.angleRad tbms.var /= c1000 tbms.const
scoreboard players operation default.math.sin.angleRad tbms.var /= c180 tbms.const
scoreboard players set default.math.sin.res tbms.var 0
scoreboard players set default.math.sin.sign tbms.var 1000
scoreboard players set default.math.sin.fact tbms.var 1000
scoreboard players set default.math.sin.pow tbms.var 1000
scoreboard players set default.math.sin._0.i tbms.var 0
execute if score default.math.sin._0.i tbms.var matches ..9 run function default:zzz_sl_block/6
scoreboard players operation default.math.sin._ret tbms.var = default.math.sin.res tbms.var
