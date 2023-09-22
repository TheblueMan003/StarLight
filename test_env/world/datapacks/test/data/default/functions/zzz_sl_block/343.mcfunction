# ==================================================
# void default.zzz_sl_block.343()
# a.k.a default.test.TestRunner.next._2.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.neq_int_fail.enabled tbms.var = default.int.neq_int_fail.enabled tbms.var run scoreboard players set default.int.neq_int_fail.enabled tbms.var 0
execute if score default.int.neq_int_fail.enabled tbms.var matches 0 run function default:zzz_sl_block/196
