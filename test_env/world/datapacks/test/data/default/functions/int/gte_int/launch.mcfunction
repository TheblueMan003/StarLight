# ==================================================
# void default.int.gte_int.launch()
# ==================================================

execute unless score default.int.gte_int.enabled tbms.var = default.int.gte_int.enabled tbms.var run scoreboard players set default.int.gte_int.enabled tbms.var 0
execute if score default.int.gte_int.enabled tbms.var matches 0 run function default:zzz_sl_block/267
