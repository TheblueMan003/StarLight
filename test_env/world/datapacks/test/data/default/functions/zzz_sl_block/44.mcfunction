# ==================================================
# void default.zzz_sl_block.44()
# a.k.a default.int.ternary_operator_mixed_result.start.1
# ==================================================

scoreboard players set default.int.ternary_operator_mixed_result.enabled tbms.var 1
scoreboard players set default.int.ternary_operator_mixed_result.time tbms.var 0
execute unless score default.int.ternary_operator_mixed_result.enabled tbms.var matches 0 run function default:zzz_sl_block/42
