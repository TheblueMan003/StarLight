# ==================================================
# void default.zzz_sl_block.6()
# a.k.a default.int.tuple_unpacking_nested_tuples.start.1
# ==================================================

scoreboard players set default.int.tuple_unpacking_nested_tuples.enabled tbms.var 1
scoreboard players set default.int.tuple_unpacking_nested_tuples.time tbms.var 0
execute unless score default.int.tuple_unpacking_nested_tuples.enabled tbms.var matches 0 run function default:zzz_sl_block/4
