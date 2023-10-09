# ==================================================
# void default.int.not_addition_variable.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.not_addition_variable.crashCount tbms.var 1
scoreboard players set default.int.not_addition_variable.crash._0 tbms.var 0
execute if score default.int.not_addition_variable.crashCount tbms.var matches 11.. run function default:zzz_sl_block/189
execute if score default.int.not_addition_variable.crash._0 tbms.var matches 0 unless score default.int.not_addition_variable.enabled tbms.var matches 0 run function default:zzz_sl_block/186
