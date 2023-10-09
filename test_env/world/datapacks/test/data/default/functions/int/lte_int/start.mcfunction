# ==================================================
# void default.int.lte_int.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.lte_int.enabled tbms.var = default.int.lte_int.enabled tbms.var run scoreboard players set default.int.lte_int.enabled tbms.var 0
execute if score default.int.lte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/275
