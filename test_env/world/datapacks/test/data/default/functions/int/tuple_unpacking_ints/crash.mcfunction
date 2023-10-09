# ==================================================
# void default.int.tuple_unpacking_ints.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.int.tuple_unpacking_ints.crashCount tbms.var 1
scoreboard players set default.int.tuple_unpacking_ints.crash._0 tbms.var 0
execute if score default.int.tuple_unpacking_ints.crashCount tbms.var matches 11.. run function default:zzz_sl_block/28
execute if score default.int.tuple_unpacking_ints.crash._0 tbms.var matches 0 unless score default.int.tuple_unpacking_ints.enabled tbms.var matches 0 run function default:zzz_sl_block/25
