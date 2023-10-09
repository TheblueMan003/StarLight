# ==================================================
# void default.zzz_sl_block.90()
# a.k.a default.cmd.schedule.66.__lambda__
# ==================================================

schedule function default:int/not_eq_float/crash 1 append
execute unless score default.int.not_eq_float.enabled tbms.var matches 0 run function default:int/not_eq_float/main
schedule clear default:int/not_eq_float/crash
scoreboard players set default.int.not_eq_float.crashCount tbms.var 0
execute unless score default.int.not_eq_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/90 1 append
