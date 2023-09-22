# ==================================================
# void default.zzz_sl_block.299()
# a.k.a default.int.gt_int.start.1
# ==================================================

scoreboard players set default.int.gt_int.enabled tbms.var 1
scoreboard players set default.int.gt_int.time tbms.var 0
execute unless score default.int.gt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/297
