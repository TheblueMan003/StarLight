# ==================================================
# void default.zzz_sl_block.188()
# a.k.a default.int.not_addition_variable.start.1
# ==================================================

scoreboard players set default.int.not_addition_variable.enabled tbms.var 1
scoreboard players set default.int.not_addition_variable.time tbms.var 0
execute unless score default.int.not_addition_variable.enabled tbms.var matches 0 run function default:zzz_sl_block/186
