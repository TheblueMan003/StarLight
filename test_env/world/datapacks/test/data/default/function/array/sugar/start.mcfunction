# ==================================================
# void default.array.sugar.start()
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.array.sugar.enabled tbms.var = default.array.sugar.enabled tbms.var run scoreboard players set default.array.sugar.enabled tbms.var 0
execute if score default.array.sugar.enabled tbms.var matches 0 run function default:zzz_sl_block/111
