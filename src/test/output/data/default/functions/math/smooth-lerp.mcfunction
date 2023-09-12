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
scoreboard players set default.math.smoothLerp.value tbms.var 1000
scoreboard players operation default.math.smoothLerp.value tbms.var -= default.math.smoothLerp.w tbms.var
scoreboard players operation default.math.smoothLerp.value tbms.var *= default.math.smoothLerp.a0 tbms.var
scoreboard players operation default.math.smoothLerp.value tbms.var /= c1000 tbms.const
scoreboard players operation default.math.smoothLerp._3 tbms.var = default.math.smoothLerp.w tbms.var
scoreboard players operation default.math.smoothLerp._3 tbms.var *= default.math.smoothLerp.a1 tbms.var
scoreboard players operation default.math.smoothLerp._3 tbms.var /= c1000 tbms.const
scoreboard players operation default.math.smoothLerp.value tbms.var += default.math.smoothLerp._3 tbms.var
scoreboard players operation default.math.sorted---444556297.impl.a tbms.var = default.math.smoothLerp.a0 tbms.var
scoreboard players operation default.math.sorted---444556297.impl.b tbms.var = default.math.smoothLerp.a1 tbms.var
function default:zzz_sl_block/7
scoreboard players operation default.math.smoothLerp.s._0 tbms.var = default.math.sorted---444556297.impl._ret._0 tbms.var
scoreboard players operation default.math.smoothLerp.s._1 tbms.var = default.math.sorted---444556297.impl._ret._1 tbms.var
execute if score default.math.smoothLerp.value tbms.var < default.math.smoothLerp.s._0 tbms.var run scoreboard players operation default.math.smoothLerp.value tbms.var = default.math.smoothLerp.s._0 tbms.var
execute if score default.math.smoothLerp.value tbms.var > default.math.smoothLerp.s._1 tbms.var run scoreboard players operation default.math.smoothLerp.value tbms.var = default.math.smoothLerp.s._1 tbms.var
scoreboard players operation default.math.smoothLerp._ret tbms.var = default.math.smoothLerp.value tbms.var
