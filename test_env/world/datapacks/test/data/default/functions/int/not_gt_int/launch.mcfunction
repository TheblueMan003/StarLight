# ==================================================
# void default.int.not_gt_int.launch()
# ==================================================

execute unless score default.int.not_gt_int.enabled tbms.var = default.int.not_gt_int.enabled tbms.var run scoreboard players set default.int.not_gt_int.enabled tbms.var 0
execute if score default.int.not_gt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/148
