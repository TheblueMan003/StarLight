# ==================================================
# void default.zzz_sl_block.291()
# a.k.a default.int.eq_int.start.1
# ==================================================

scoreboard players set default.int.eq_int.enabled tbms.var 1
scoreboard players set default.int.eq_int.time tbms.var 0
execute unless score default.int.eq_int.enabled tbms.var matches 0 run function default:zzz_sl_block/289
