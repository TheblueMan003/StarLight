# ==================================================
# void default.int.not_gt_float.launch()
# ==================================================

execute unless score default.int.not_gt_float.enabled tbms.var = default.int.not_gt_float.enabled tbms.var run scoreboard players set default.int.not_gt_float.enabled tbms.var 0
execute if score default.int.not_gt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/100
