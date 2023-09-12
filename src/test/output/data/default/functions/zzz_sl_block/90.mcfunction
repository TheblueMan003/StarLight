# ==================================================
# float default.zzz_sl_block.90(T? a, T? b)
# a.k.a default.math.min---444556297.impl
# ==================================================
# ==================================================
# Return the min between the a and b
# ==================================================

execute if score default.math.min---444556297.impl.a tbms.var < default.math.min---444556297.impl.b tbms.var run scoreboard players operation default.math.min---444556297.impl.b tbms.var = default.math.min---444556297.impl.a tbms.var
scoreboard players operation default.math.min---444556297.impl._ret tbms.var = default.math.min---444556297.impl.b tbms.var
