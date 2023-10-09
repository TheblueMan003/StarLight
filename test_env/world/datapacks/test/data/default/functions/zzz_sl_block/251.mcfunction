# ==================================================
# void default.zzz_sl_block.251()
# a.k.a default.int.gt_float.start.1
# ==================================================

scoreboard players set default.int.gt_float.enabled tbms.var 1
scoreboard players set default.int.gt_float.time tbms.var 0
execute unless score default.int.gt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/249
