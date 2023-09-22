# ==================================================
# void default.zzz_sl_block.354()
# a.k.a default.test.TestRunner.next._13.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.tuple_unpacking_mixed_types.enabled tbms.var = default.int.tuple_unpacking_mixed_types.enabled tbms.var run scoreboard players set default.int.tuple_unpacking_mixed_types.enabled tbms.var 0
execute if score default.int.tuple_unpacking_mixed_types.enabled tbms.var matches 0 run function default:zzz_sl_block/13
