# ==================================================
# void default.int.ternary_operator_nested.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.ternary_operator_nested.enabled tbms.var = default.int.ternary_operator_nested.enabled tbms.var run scoreboard players set default.int.ternary_operator_nested.enabled tbms.var 0
execute if score default.int.ternary_operator_nested.enabled tbms.var matches 0 run function default:zzz_sl_block/36
