# ==================================================
# void default.int.eq_float.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.eq_float.crashCount tbms.var 1
scoreboard players set default.int.eq_float.crash._0 tbms.var 0
execute if score default.int.eq_float.crashCount tbms.var matches 11.. run function default:zzz_sl_block/244
execute if score default.int.eq_float.crash._0 tbms.var matches 0 unless score default.int.eq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/241
