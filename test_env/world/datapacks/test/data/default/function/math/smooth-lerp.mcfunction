# ==================================================
# float default.math.smoothLerp(float a0, float a1, float w)
# ==================================================
# ==================================================
# Return the 3rd degree interpolated value of a0 and a1 with coefficient w
# ==================================================

scoreboard players operation default.math.smoothLerp._0 tbms.var = default.math.smoothLerp.w tbms.var
scoreboard players operation default.math.smoothLerp._0 tbms.var *= default.math.smoothLerp.w tbms.var
scoreboard players operation default.math.smoothLerp._0 tbms.var /= c1000 tbms.const
scoreboard players set default.math.smoothLerp._1 tbms.var 3000
scoreboard players set default.math.smoothLerp._2 tbms.var 2000
scoreboard players operation default.math.smoothLerp._2 tbms.var *= default.math.smoothLerp.w tbms.var
scoreboard players operation default.math.smoothLerp._2 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.smoothLerp._1 tbms.var -= default.math.smoothLerp._2 tbms.var
scoreboard players operation default.math.smoothLerp._0 tbms.var *= default.math.smoothLerp._1 tbms.var
scoreboard players operation default.math.smoothLerp._0 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.smoothLerp.w tbms.var = default.math.smoothLerp._0 tbms.var
