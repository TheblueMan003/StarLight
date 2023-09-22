# ==================================================
# void default.zzz_sl_block.36()
# a.k.a default.int.ternary_operator_nested.start.1
# ==================================================

scoreboard players set default.int.ternary_operator_nested.enabled tbms.var 1
scoreboard players set default.int.ternary_operator_nested.time tbms.var 0
execute unless score default.int.ternary_operator_nested.enabled tbms.var matches 0 run function default:zzz_sl_block/34
