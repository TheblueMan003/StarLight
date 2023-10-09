# ==================================================
# void default.zzz_sl_block.108()
# a.k.a default.int.not_lt_float.start.1
# ==================================================

scoreboard players set default.int.not_lt_float.enabled tbms.var 1
scoreboard players set default.int.not_lt_float.time tbms.var 0
execute unless score default.int.not_lt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/106
