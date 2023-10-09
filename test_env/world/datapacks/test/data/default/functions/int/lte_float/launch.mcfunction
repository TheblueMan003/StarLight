# ==================================================
# void default.int.lte_float.launch()
# ==================================================

execute unless score default.int.lte_float.enabled tbms.var = default.int.lte_float.enabled tbms.var run scoreboard players set default.int.lte_float.enabled tbms.var 0
execute if score default.int.lte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/227
