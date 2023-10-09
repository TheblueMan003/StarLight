# ==================================================
# void default.zzz_sl_block.379()
# a.k.a default.test.TestRunner.next._38.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.not_lt_float.enabled tbms.var = default.int.not_lt_float.enabled tbms.var run scoreboard players set default.int.not_lt_float.enabled tbms.var 0
execute if score default.int.not_lt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/108
