# ==================================================
# void default.zzz_sl_block.243()
# a.k.a default.int.eq_float.start.1
# ==================================================

scoreboard players set default.int.eq_float.enabled tbms.var 1
scoreboard players set default.int.eq_float.time tbms.var 0
execute unless score default.int.eq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/241
