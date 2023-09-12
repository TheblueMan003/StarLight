# ==================================================
# float default.math.acos(float angle)
# ==================================================
# ==================================================
# Compute the arccossinus of the angle in degrees with a taylor series
# ==================================================

scoreboard players set default.math.acos._ret tbms.var 90000
scoreboard players operation default.math.asin.angle tbms.var = default.math.acos.angle tbms.var
function default:math/asin
scoreboard players operation default.math.acos._ret tbms.var -= default.math.asin._ret tbms.var
