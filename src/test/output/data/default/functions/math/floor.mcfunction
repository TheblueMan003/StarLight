# ==================================================
# float default.math.floor(float value)
# ==================================================
# ==================================================
# Round float to lowest value
# ==================================================

scoreboard players operation default.math.floor.value tbms.var /= c1000 tbms.const
scoreboard players operation default.math.floor.value tbms.var *= c1000 tbms.const
scoreboard players operation default.math.floor._ret tbms.var = default.math.floor.value tbms.var
