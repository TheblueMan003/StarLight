# ==================================================
# (float,float) default.zzz_sl_block.7(T? a, T? b)
# a.k.a default.math.sorted---444556297.impl
# ==================================================
# ==================================================
# Return (a,b) if a <= b else return (b,a)
# ==================================================

scoreboard players set default.math.sorted---444556297.impl._0 tbms.var 0
execute if score default.math.sorted---444556297.impl.a tbms.var > default.math.sorted---444556297.impl.b tbms.var run function default:zzz_sl_block/8
execute if score default.math.sorted---444556297.impl._0 tbms.var matches 0 run function default:zzz_sl_block/9
