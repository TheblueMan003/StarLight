# ==================================================
# void default.zzz_sl_block.339()
# a.k.a default.int.addition_variable.start.1
# ==================================================

scoreboard players set default.int.addition_variable.enabled tbms.var 1
scoreboard players set default.int.addition_variable.time tbms.var 0
execute unless score default.int.addition_variable.enabled tbms.var matches 0 run function default:zzz_sl_block/337
