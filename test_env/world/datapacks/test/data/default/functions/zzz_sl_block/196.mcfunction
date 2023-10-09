# ==================================================
# void default.zzz_sl_block.196()
# a.k.a default.int.neq_int_fail.start.1
# ==================================================

scoreboard players set default.int.neq_int_fail.enabled tbms.var 1
scoreboard players set default.int.neq_int_fail.time tbms.var 0
execute unless score default.int.neq_int_fail.enabled tbms.var matches 0 run function default:zzz_sl_block/194
