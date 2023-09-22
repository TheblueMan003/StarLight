# ==================================================
# void default.zzz_sl_block.249()
# a.k.a default.cmd.schedule.186.__lambda__
# ==================================================

schedule function default:int/gt_float/crash 1 append
execute unless score default.int.gt_float.enabled tbms.var matches 0 run function default:int/gt_float/main
schedule clear default:int/gt_float/crash
scoreboard players set default.int.gt_float.crashCount tbms.var 0
execute unless score default.int.gt_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/249 1 append
