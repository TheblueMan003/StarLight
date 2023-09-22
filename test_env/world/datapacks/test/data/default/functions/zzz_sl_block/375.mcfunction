# ==================================================
# void default.zzz_sl_block.375()
# a.k.a default.test.TestRunner.next._34.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.neq_float.enabled tbms.var = default.int.neq_float.enabled tbms.var run scoreboard players set default.int.neq_float.enabled tbms.var 0
execute if score default.int.neq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/235
