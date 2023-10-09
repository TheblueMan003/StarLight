# ==================================================
# void default.zzz_sl_block.219()
# a.k.a default.int.gte_float.start.1
# ==================================================

scoreboard players set default.int.gte_float.enabled tbms.var 1
scoreboard players set default.int.gte_float.time tbms.var 0
execute unless score default.int.gte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/217
