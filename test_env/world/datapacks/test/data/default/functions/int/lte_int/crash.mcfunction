# ==================================================
# void default.int.lte_int.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.lte_int.crashCount tbms.var 1
scoreboard players set default.int.lte_int.crash._0 tbms.var 0
execute if score default.int.lte_int.crashCount tbms.var matches 11.. run function default:zzz_sl_block/276
execute if score default.int.lte_int.crash._0 tbms.var matches 0 unless score default.int.lte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/273
