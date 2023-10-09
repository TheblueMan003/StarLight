# ==================================================
# void default.zzz_sl_block.377()
# a.k.a default.test.TestRunner.next._36.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.tuple_unpacking_nested_tuples.enabled tbms.var = default.int.tuple_unpacking_nested_tuples.enabled tbms.var run scoreboard players set default.int.tuple_unpacking_nested_tuples.enabled tbms.var 0
execute if score default.int.tuple_unpacking_nested_tuples.enabled tbms.var matches 0 run function default:zzz_sl_block/6
