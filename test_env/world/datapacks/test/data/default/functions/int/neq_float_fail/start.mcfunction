# ==================================================
# void default.int.neq_float_fail.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.int.neq_float_fail.enabled tbms.var = default.int.neq_float_fail.enabled tbms.var run scoreboard players set default.int.neq_float_fail.enabled tbms.var 0
execute if score default.int.neq_float_fail.enabled tbms.var matches 0 run function default:zzz_sl_block/204
