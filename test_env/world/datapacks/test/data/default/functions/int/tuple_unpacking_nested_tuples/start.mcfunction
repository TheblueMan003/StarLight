# ==================================================
# void default.int.tuple_unpacking_nested_tuples.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.tuple_unpacking_nested_tuples.enabled tbms.var = default.int.tuple_unpacking_nested_tuples.enabled tbms.var run scoreboard players set default.int.tuple_unpacking_nested_tuples.enabled tbms.var 0
execute if score default.int.tuple_unpacking_nested_tuples.enabled tbms.var matches 0 run function default:zzz_sl_block/6
