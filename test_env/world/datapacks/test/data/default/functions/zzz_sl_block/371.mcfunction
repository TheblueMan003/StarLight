# ==================================================
# void default.zzz_sl_block.371()
# a.k.a default.test.TestRunner.next._30.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.tuple_unpacking_floats.enabled tbms.var = default.int.tuple_unpacking_floats.enabled tbms.var run scoreboard players set default.int.tuple_unpacking_floats.enabled tbms.var 0
execute if score default.int.tuple_unpacking_floats.enabled tbms.var matches 0 run function default:zzz_sl_block/20
