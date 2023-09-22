# ==================================================
# void default.int.gt_float.main()
# ==================================================

scoreboard players add default.int.gt_float.time tbms.var 1
scoreboard players set default.int.gt_float.main._0 tbms.var 2
execute if score default.int.gt_float.time tbms.var >= default.int.gt_float.main._0 tbms.var unless score default.int.gt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/248
