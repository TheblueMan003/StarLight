# ==================================================
# int default.utils.process_manager.count()
# ==================================================
# ==================================================
# Return the number of currently active processes
# ==================================================

scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:array/sugar/__count__
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:array/initer/__count__
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:array/addition/__count__
scoreboard players set default.utils.process_manager.t_running tbms.var 0
scoreboard players set default.utils.process_manager.t_total tbms.var 0
function default:test/-test-runner/__count__
