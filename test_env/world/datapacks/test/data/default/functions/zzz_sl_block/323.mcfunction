# ==================================================
# void default.zzz_sl_block.323()
# a.k.a default.int.addition_value.start.1
# ==================================================

scoreboard players set default.int.addition_value.enabled tbms.var 1
scoreboard players set default.int.addition_value.time tbms.var 0
execute unless score default.int.addition_value.enabled tbms.var matches 0 run function default:zzz_sl_block/321
