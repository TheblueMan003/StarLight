# ==================================================
# void default.int.tuple_unpacking_floats.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.tuple_unpacking_floats.enabled tbms.var = default.int.tuple_unpacking_floats.enabled tbms.var run scoreboard players set default.int.tuple_unpacking_floats.enabled tbms.var 0
execute if score default.int.tuple_unpacking_floats.enabled tbms.var matches 0 run function default:zzz_sl_block/20
