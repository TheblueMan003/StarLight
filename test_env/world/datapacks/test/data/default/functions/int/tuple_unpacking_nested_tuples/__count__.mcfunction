# ==================================================
# void default.int.tuple_unpacking_nested_tuples.__count__()
# ==================================================
# ==================================================
#     Count the number of active process    
# ==================================================

scoreboard players add default.utils.process_manager.t_total tbms.var 1
scoreboard players operation default.utils.process_manager.t_running tbms.var += default.int.tuple_unpacking_nested_tuples.enabled tbms.var