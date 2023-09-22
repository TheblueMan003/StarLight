# ==================================================
# void default.int.ternary_operator_float_result.main()
# ==================================================

scoreboard players add default.int.ternary_operator_float_result.time tbms.var 1
scoreboard players set default.int.ternary_operator_float_result.main._0 tbms.var 2
execute if score default.int.ternary_operator_float_result.time tbms.var >= default.int.ternary_operator_float_result.main._0 tbms.var unless score default.int.ternary_operator_float_result.enabled tbms.var matches 0 run function default:zzz_sl_block/49
