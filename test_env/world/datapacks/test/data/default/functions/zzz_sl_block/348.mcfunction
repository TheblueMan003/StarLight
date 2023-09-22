# ==================================================
# void default.zzz_sl_block.348()
# a.k.a default.test.TestRunner.next._7.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.lte_float.enabled tbms.var = default.int.lte_float.enabled tbms.var run scoreboard players set default.int.lte_float.enabled tbms.var 0
execute if score default.int.lte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/227
