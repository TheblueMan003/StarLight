# ==================================================
# void default.zzz_sl_block.307()
# a.k.a default.int.lt_int.start.1
# ==================================================

scoreboard players set default.int.lt_int.enabled tbms.var 1
scoreboard players set default.int.lt_int.time tbms.var 0
execute unless score default.int.lt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/305
