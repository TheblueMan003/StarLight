# ==================================================
# void default.zzz_sl_block.146()
# a.k.a default.cmd.schedule.108.__lambda__
# ==================================================

schedule function default:int/not_gt_int/crash 1 append
execute unless score default.int.not_gt_int.enabled tbms.var matches 0 run function default:int/not_gt_int/main
schedule clear default:int/not_gt_int/crash
scoreboard players set default.int.not_gt_int.crashCount tbms.var 0
execute unless score default.int.not_gt_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/146 1 append
