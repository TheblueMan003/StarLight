# ==================================================
# void default.zzz_sl_block.374()
# a.k.a default.test.TestRunner.next._33.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.lt_int.enabled tbms.var = default.int.lt_int.enabled tbms.var run scoreboard players set default.int.lt_int.enabled tbms.var 0
execute if score default.int.lt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/307