# ==================================================
# void default.int.not_addition_value.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.not_addition_value.enabled tbms.var = default.int.not_addition_value.enabled tbms.var run scoreboard players set default.int.not_addition_value.enabled tbms.var 0
execute if score default.int.not_addition_value.enabled tbms.var matches 0 run function default:zzz_sl_block/172
