# ==================================================
# void default.zzz_sl_block.257()
# a.k.a default.cmd.schedule.192.__lambda__
# ==================================================

schedule function default:int/lt_float/crash 1 append
execute unless score default.int.lt_float.enabled tbms.var matches 0 run function default:int/lt_float/main
schedule clear default:int/lt_float/crash
scoreboard players set default.int.lt_float.crashCount tbms.var 0
execute unless score default.int.lt_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/257 1 append
