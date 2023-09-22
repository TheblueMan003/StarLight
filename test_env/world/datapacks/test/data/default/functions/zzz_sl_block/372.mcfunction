# ==================================================
# void default.zzz_sl_block.372()
# a.k.a default.test.TestRunner.next._31.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.not_lt_int.enabled tbms.var = default.int.not_lt_int.enabled tbms.var run scoreboard players set default.int.not_lt_int.enabled tbms.var 0
execute if score default.int.not_lt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/156
