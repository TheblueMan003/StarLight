# ==================================================
# void default.zzz_sl_block.194()
# a.k.a default.test.TestRunner.next._1.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.array.initer.enabled tbms.var = default.array.initer.enabled tbms.var run scoreboard players set default.array.initer.enabled tbms.var 0
execute if score default.array.initer.enabled tbms.var matches 0 run function default:zzz_sl_block/191
