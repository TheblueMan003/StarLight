# ==================================================
# float default.math.tan(float angle)
# ==================================================
# ==================================================
# Compute the tangente of the angle in degrees with a taylor series
# ==================================================

scoreboard players operation default.math.sin.angle tbms.var = default.math.tan.angle tbms.var
function default:math/sin
scoreboard players operation default.math.tan._ret tbms.var = default.math.sin._ret tbms.var
scoreboard players operation default.math.cos.angle tbms.var = default.math.tan.angle tbms.var
function default:math/cos
scoreboard players operation default.math.tan._ret tbms.var /= default.math.cos._ret tbms.var
scoreboard players operation default.math.tan._ret tbms.var *= c1000 tbms.const
