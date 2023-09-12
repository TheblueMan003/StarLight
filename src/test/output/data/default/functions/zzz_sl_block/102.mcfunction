# ==================================================
# float default.zzz_sl_block.102(T? x)
# a.k.a default.math.abs---444556297.impl
# ==================================================
# ==================================================
# Return the absolute value of x
# ==================================================

execute if score default.math.abs---444556297.impl.x tbms.var matches ..-1 run scoreboard players operation default.math.abs---444556297.impl.x tbms.var *= c-1 tbms.const
scoreboard players operation default.math.abs---444556297.impl._ret tbms.var = default.math.abs---444556297.impl.x tbms.var
