# ==================================================
# void default.zzz_sl_block.235()
# a.k.a default.int.neq_float.start.1
# ==================================================

scoreboard players set default.int.neq_float.enabled tbms.var 1
scoreboard players set default.int.neq_float.time tbms.var 0
execute unless score default.int.neq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/233
