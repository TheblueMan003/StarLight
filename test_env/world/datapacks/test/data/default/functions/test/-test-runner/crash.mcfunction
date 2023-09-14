# ==================================================
# void default.test.TestRunner.crash()
# ==================================================
# ==================================================
#     Detect maxCommandChainLength extended, and stop process if more than 10 in a row    
# ==================================================

scoreboard players add default.test.TestRunner.crashCount tbms.var 1
scoreboard players set default.test.TestRunner.crash._0 tbms.var 0
execute if score default.test.TestRunner.crashCount tbms.var matches 11.. run function default:zzz_sl_block/325
execute if score default.test.TestRunner.crash._0 tbms.var matches 0 unless score default.test.TestRunner.enabled tbms.var matches 0 run function default:zzz_sl_block/322
