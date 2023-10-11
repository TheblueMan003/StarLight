# ==================================================
# void default.zzz_sl_block.6()
# a.k.a default.test.test.a.crash
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.test.test.a.crashCount tbms.var 1
scoreboard players set default.test.test.a.crash._0 tbms.var 0
execute if score default.test.test.a.crashCount tbms.var matches 11.. run function default:zzz_sl_block/12
execute if score default.test.test.a.crash._0 tbms.var matches 0 run function default:zzz_sl_block/10
