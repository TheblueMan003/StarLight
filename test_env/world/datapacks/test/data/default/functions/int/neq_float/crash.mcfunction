# ==================================================
# void default.int.neq_float.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.neq_float.crashCount tbms.var 1
scoreboard players set default.int.neq_float.crash._0 tbms.var 0
execute if score default.int.neq_float.crashCount tbms.var matches 11.. run function default:zzz_sl_block/236
execute if score default.int.neq_float.crash._0 tbms.var matches 0 unless score default.int.neq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/233
