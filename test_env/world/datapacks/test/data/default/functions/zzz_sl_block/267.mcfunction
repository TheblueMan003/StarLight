# ==================================================
# void default.zzz_sl_block.267()
# a.k.a default.int.gte_int.start.1
# ==================================================

scoreboard players set default.int.gte_int.enabled tbms.var 1
scoreboard players set default.int.gte_int.time tbms.var 0
execute unless score default.int.gte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/265
