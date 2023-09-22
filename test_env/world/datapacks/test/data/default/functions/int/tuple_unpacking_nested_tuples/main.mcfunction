# ==================================================
# void default.int.tuple_unpacking_nested_tuples.main()
# ==================================================

scoreboard players add default.int.tuple_unpacking_nested_tuples.time tbms.var 1
scoreboard players set default.int.tuple_unpacking_nested_tuples.main._0 tbms.var 2
execute if score default.int.tuple_unpacking_nested_tuples.time tbms.var >= default.int.tuple_unpacking_nested_tuples.main._0 tbms.var unless score default.int.tuple_unpacking_nested_tuples.enabled tbms.var matches 0 run function default:zzz_sl_block/3
