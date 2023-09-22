# ==================================================
# void default.zzz_sl_block.259()
# a.k.a default.int.lt_float.start.1
# ==================================================

scoreboard players set default.int.lt_float.enabled tbms.var 1
scoreboard players set default.int.lt_float.time tbms.var 0
execute unless score default.int.lt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/257
