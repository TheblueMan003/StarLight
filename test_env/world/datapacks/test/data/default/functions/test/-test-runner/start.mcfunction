# ==================================================
# void default.test.TestRunner.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.test.TestRunner.enabled tbms.var = default.test.TestRunner.enabled tbms.var run scoreboard players set default.test.TestRunner.enabled tbms.var 0
execute if score default.test.TestRunner.enabled tbms.var matches 0 run function default:zzz_sl_block/387
