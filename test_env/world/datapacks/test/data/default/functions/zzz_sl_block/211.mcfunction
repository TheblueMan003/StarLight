# ==================================================
# void default.zzz_sl_block.211()
# a.k.a default.int.neq_float_delta.start.1
# ==================================================

scoreboard players set default.int.neq_float_delta.enabled tbms.var 1
scoreboard players set default.int.neq_float_delta.time tbms.var 0
execute unless score default.int.neq_float_delta.enabled tbms.var matches 0 run function default:zzz_sl_block/209
