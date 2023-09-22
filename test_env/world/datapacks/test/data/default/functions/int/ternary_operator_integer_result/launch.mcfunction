# ==================================================
# void default.int.ternary_operator_integer_result.launch()
# ==================================================

execute unless score default.int.ternary_operator_integer_result.enabled tbms.var = default.int.ternary_operator_integer_result.enabled tbms.var run scoreboard players set default.int.ternary_operator_integer_result.enabled tbms.var 0
execute if score default.int.ternary_operator_integer_result.enabled tbms.var matches 0 run function default:zzz_sl_block/60
