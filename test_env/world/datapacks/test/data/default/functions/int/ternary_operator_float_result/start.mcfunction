# ==================================================
# void default.int.ternary_operator_float_result.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.ternary_operator_float_result.enabled tbms.var = default.int.ternary_operator_float_result.enabled tbms.var run scoreboard players set default.int.ternary_operator_float_result.enabled tbms.var 0
execute if score default.int.ternary_operator_float_result.enabled tbms.var matches 0 run function default:zzz_sl_block/52
