# ==================================================
# void default.int.ternary_operator_integer_result.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.ternary_operator_integer_result.crashCount tbms.var 1
scoreboard players set default.int.ternary_operator_integer_result.crash._0 tbms.var 0
execute if score default.int.ternary_operator_integer_result.crashCount tbms.var matches 11.. run function default:zzz_sl_block/61
execute if score default.int.ternary_operator_integer_result.crash._0 tbms.var matches 0 unless score default.int.ternary_operator_integer_result.enabled tbms.var matches 0 run function default:zzz_sl_block/58
