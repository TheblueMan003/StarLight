# ==================================================
# void default.zzz_sl_block.76()
# a.k.a default.int.not_lte_float.start.1
# ==================================================

scoreboard players set default.int.not_lte_float.enabled tbms.var 1
scoreboard players set default.int.not_lte_float.time tbms.var 0
execute unless score default.int.not_lte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/74
