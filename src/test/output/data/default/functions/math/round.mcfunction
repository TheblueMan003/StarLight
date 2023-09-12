# ==================================================
# float default.math.round(float value)
# ==================================================
# ==================================================
# Round float to closest value
# ==================================================

scoreboard players add default.math.round.value tbms.var 500
scoreboard players operation default.math.round.value tbms.var /= c1000 tbms.const
scoreboard players operation default.math.round.value tbms.var *= c1000 tbms.const
scoreboard players operation default.math.round._ret tbms.var = default.math.round.value tbms.var
