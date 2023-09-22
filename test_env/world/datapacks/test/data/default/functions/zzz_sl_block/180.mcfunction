# ==================================================
# void default.zzz_sl_block.180()
# a.k.a default.int.not_multi_variable.start.1
# ==================================================

scoreboard players set default.int.not_multi_variable.enabled tbms.var 1
scoreboard players set default.int.not_multi_variable.time tbms.var 0
execute unless score default.int.not_multi_variable.enabled tbms.var matches 0 run function default:zzz_sl_block/178
