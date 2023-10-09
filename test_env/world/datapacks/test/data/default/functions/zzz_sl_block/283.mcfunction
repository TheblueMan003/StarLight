# ==================================================
# void default.zzz_sl_block.283()
# a.k.a default.int.neq_int.start.1
# ==================================================

scoreboard players set default.int.neq_int.enabled tbms.var 1
scoreboard players set default.int.neq_int.time tbms.var 0
execute unless score default.int.neq_int.enabled tbms.var matches 0 run function default:zzz_sl_block/281
