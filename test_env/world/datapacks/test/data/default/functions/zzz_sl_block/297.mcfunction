# ==================================================
# void default.zzz_sl_block.297()
# a.k.a default.cmd.schedule.222.__lambda__
# ==================================================

schedule function default:int/gt_int/crash 1 append
execute unless score default.int.gt_int.enabled tbms.var matches 0 run function default:int/gt_int/main
schedule clear default:int/gt_int/crash
scoreboard players set default.int.gt_int.crashCount tbms.var 0
execute unless score default.int.gt_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/297 1 append
