# ==================================================
# void default.int.gt_float.launch()
# ==================================================

execute unless score default.int.gt_float.enabled tbms.var = default.int.gt_float.enabled tbms.var run scoreboard players set default.int.gt_float.enabled tbms.var 0
execute if score default.int.gt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/251
