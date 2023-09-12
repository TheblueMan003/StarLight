# ==================================================
# float default.math.atan2(float y, float x)
# ==================================================
# ==================================================
# Compute the arctangente of the angle in degrees with a taylor series
# ==================================================

scoreboard players operation default.math.atan.angle tbms.var = default.math.atan2.y tbms.var
scoreboard players operation default.math.atan.angle tbms.var /= default.math.atan2.x tbms.var
scoreboard players operation default.math.atan.angle tbms.var *= c1000 tbms.const
function default:math/atan
scoreboard players operation default.math.atan2._ret tbms.var = default.math.atan._ret tbms.var
