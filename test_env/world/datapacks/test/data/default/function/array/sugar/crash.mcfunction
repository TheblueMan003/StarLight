# ==================================================
# void default.array.sugar.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.array.sugar.crashCount tbms.var 1
scoreboard players set default.array.sugar.crash._0 tbms.var 0
execute if score default.array.sugar.crashCount tbms.var matches 11.. run function default:zzz_sl_block/112
execute if score default.array.sugar.crash._0 tbms.var matches 0 unless score default.array.sugar.enabled tbms.var matches 0 run function default:zzz_sl_block/109
