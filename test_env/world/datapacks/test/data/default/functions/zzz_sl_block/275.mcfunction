# ==================================================
# void default.zzz_sl_block.275()
# a.k.a default.int.lte_int.start.1
# ==================================================

scoreboard players set default.int.lte_int.enabled tbms.var 1
scoreboard players set default.int.lte_int.time tbms.var 0
execute unless score default.int.lte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/273
