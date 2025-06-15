# ==================================================
# void default.zzz_sl_block.193()
# a.k.a default.test.TestRunner.next._0.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.array.addition.enabled tbms.var = default.array.addition.enabled tbms.var run scoreboard players set default.array.addition.enabled tbms.var 0
execute if score default.array.addition.enabled tbms.var matches 0 run function default:zzz_sl_block/167
