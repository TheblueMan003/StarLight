# ==================================================
# void default.zzz_sl_block.368()
# a.k.a default.test.TestRunner.next._27.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.not_neq_float.enabled tbms.var = default.int.not_neq_float.enabled tbms.var run scoreboard players set default.int.not_neq_float.enabled tbms.var 0
execute if score default.int.not_neq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/84
