# ==================================================
# float default.math.linearLerp(float a0, float a1, float w)
# ==================================================
# ==================================================
# Return the linear interpolated value of a0 and a1 with coefficient w
# ==================================================

scoreboard players set default.math.linearLerp._ret tbms.var 1000
scoreboard players operation default.math.linearLerp._ret tbms.var -= default.math.linearLerp.w tbms.var
scoreboard players operation default.math.linearLerp._ret tbms.var *= default.math.linearLerp.a0 tbms.var
scoreboard players operation default.math.linearLerp._ret tbms.var /= c1000 tbms.const
scoreboard players operation default.math.linearLerp._0 tbms.var = default.math.linearLerp.a1 tbms.var
scoreboard players operation default.math.linearLerp._0 tbms.var *= default.math.linearLerp.w tbms.var
scoreboard players operation default.math.linearLerp._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.linearLerp._ret tbms.var += default.math.linearLerp._0 tbms.var
