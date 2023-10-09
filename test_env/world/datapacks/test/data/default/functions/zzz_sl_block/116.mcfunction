# ==================================================
# void default.zzz_sl_block.116()
# a.k.a default.int.not_gte_int.start.1
# ==================================================

scoreboard players set default.int.not_gte_int.enabled tbms.var 1
scoreboard players set default.int.not_gte_int.time tbms.var 0
execute unless score default.int.not_gte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/114
