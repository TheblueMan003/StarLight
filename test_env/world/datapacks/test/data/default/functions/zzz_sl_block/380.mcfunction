# ==================================================
# void default.zzz_sl_block.380()
# a.k.a default.test.TestRunner.next._39.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.eq_int.enabled tbms.var = default.int.eq_int.enabled tbms.var run scoreboard players set default.int.eq_int.enabled tbms.var 0
execute if score default.int.eq_int.enabled tbms.var matches 0 run function default:zzz_sl_block/291
