# ==================================================
# float default.math.pow-1(float x, float y)
# a.k.a default.math.pow
# ==================================================
# ==================================================
# Compute the pow of x with a taylor series
# ==================================================

scoreboard players operation default.math.exp.x tbms.var = default.math.pow-1.y tbms.var
scoreboard players operation default.math.log.x tbms.var = default.math.pow-1.x tbms.var
function default:math/log
scoreboard players operation default.math.exp.x tbms.var *= default.math.log._ret tbms.var
scoreboard players operation default.math.exp.x tbms.var /= c1000 tbms.const
function default:math/exp
scoreboard players operation default.math.pow-1._ret tbms.var = default.math.exp._ret tbms.var
