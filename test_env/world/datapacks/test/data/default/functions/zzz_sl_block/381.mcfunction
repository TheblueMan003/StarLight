# ==================================================
# void default.zzz_sl_block.381()
# a.k.a default.test.TestRunner.next._40.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.sub_value.enabled tbms.var = default.int.sub_value.enabled tbms.var run scoreboard players set default.int.sub_value.enabled tbms.var 0
execute if score default.int.sub_value.enabled tbms.var matches 0 run function default:zzz_sl_block/315
