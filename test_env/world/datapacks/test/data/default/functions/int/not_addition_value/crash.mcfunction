# ==================================================
# void default.int.not_addition_value.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.not_addition_value.crashCount tbms.var 1
scoreboard players set default.int.not_addition_value.crash._0 tbms.var 0
execute if score default.int.not_addition_value.crashCount tbms.var matches 11.. run function default:zzz_sl_block/173
execute if score default.int.not_addition_value.crash._0 tbms.var matches 0 unless score default.int.not_addition_value.enabled tbms.var matches 0 run function default:zzz_sl_block/170
