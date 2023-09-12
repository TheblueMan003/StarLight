# ==================================================
# float default.math.ceil(float value)
# ==================================================
# ==================================================
# Round float to upper value
# ==================================================

scoreboard players add default.math.ceil.value tbms.var 999
scoreboard players operation default.math.ceil.value tbms.var /= c1000 tbms.const
scoreboard players operation default.math.ceil.value tbms.var *= c1000 tbms.const
scoreboard players operation default.math.ceil._ret tbms.var = default.math.ceil.value tbms.var
