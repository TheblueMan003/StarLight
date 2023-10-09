# ==================================================
# void default.int.gte_float.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.gte_float.enabled tbms.var = default.int.gte_float.enabled tbms.var run scoreboard players set default.int.gte_float.enabled tbms.var 0
execute if score default.int.gte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/219
