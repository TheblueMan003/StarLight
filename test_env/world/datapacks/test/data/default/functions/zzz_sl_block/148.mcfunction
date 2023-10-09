# ==================================================
# void default.zzz_sl_block.148()
# a.k.a default.int.not_gt_int.start.1
# ==================================================

scoreboard players set default.int.not_gt_int.enabled tbms.var 1
scoreboard players set default.int.not_gt_int.time tbms.var 0
execute unless score default.int.not_gt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/146
