# ==================================================
# int default.utils.process_manager.count()
# ==================================================
# ==================================================
# Return the number of currently active processes
# ==================================================

scoreboard players set default.utils.process_manager.count.total tbms.var 0
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:zzz_sl_block/13
scoreboard players operation default.utils.process_manager.count.total tbms.var += default.utils.process_manager.t_running tbms.var
scoreboard players operation default.utils.process_manager.count._ret tbms.var = default.utils.process_manager.count.total tbms.var
