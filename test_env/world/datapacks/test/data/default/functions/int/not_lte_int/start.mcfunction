# ==================================================
# void default.int.not_lte_int.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.not_lte_int.enabled tbms.var = default.int.not_lte_int.enabled tbms.var run scoreboard players set default.int.not_lte_int.enabled tbms.var 0
execute if score default.int.not_lte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/124
