# ==================================================
# void default.zzz_sl_block.353()
# a.k.a default.test.TestRunner.next._12.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.multi_variable.enabled tbms.var = default.int.multi_variable.enabled tbms.var run scoreboard players set default.int.multi_variable.enabled tbms.var 0
execute if score default.int.multi_variable.enabled tbms.var matches 0 run function default:zzz_sl_block/331
