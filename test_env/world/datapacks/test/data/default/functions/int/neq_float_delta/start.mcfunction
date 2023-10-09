# ==================================================
# void default.int.neq_float_delta.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.neq_float_delta.enabled tbms.var = default.int.neq_float_delta.enabled tbms.var run scoreboard players set default.int.neq_float_delta.enabled tbms.var 0
execute if score default.int.neq_float_delta.enabled tbms.var matches 0 run function default:zzz_sl_block/211
