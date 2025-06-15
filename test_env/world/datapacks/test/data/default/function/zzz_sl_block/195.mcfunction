# ==================================================
# void default.zzz_sl_block.195()
# a.k.a default.test.TestRunner.next._2.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.array.sugar.enabled tbms.var = default.array.sugar.enabled tbms.var run scoreboard players set default.array.sugar.enabled tbms.var 0
execute if score default.array.sugar.enabled tbms.var matches 0 run function default:zzz_sl_block/111
