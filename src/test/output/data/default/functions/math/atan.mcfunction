# ==================================================
# float default.math.atan(float angle)
# ==================================================
# ==================================================
# Compute the arctangente of the angle in degrees with a taylor series
# ==================================================

scoreboard players set default.math.atan._ret tbms.var 90000
scoreboard players operation default.math.acos.angle tbms.var = default.math.atan.angle tbms.var
function default:math/acos
scoreboard players operation default.math.atan._ret tbms.var -= default.math.acos._ret tbms.var
