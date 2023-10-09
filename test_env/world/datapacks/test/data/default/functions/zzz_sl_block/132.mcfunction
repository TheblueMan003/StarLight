# ==================================================
# void default.zzz_sl_block.132()
# a.k.a default.int.not_neq_int.start.1
# ==================================================

scoreboard players set default.int.not_neq_int.enabled tbms.var 1
scoreboard players set default.int.not_neq_int.time tbms.var 0
execute unless score default.int.not_neq_int.enabled tbms.var matches 0 run function default:zzz_sl_block/130
