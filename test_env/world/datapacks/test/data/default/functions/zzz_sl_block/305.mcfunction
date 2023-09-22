# ==================================================
# void default.zzz_sl_block.305()
# a.k.a default.cmd.schedule.228.__lambda__
# ==================================================

schedule function default:int/lt_int/crash 1 append
execute unless score default.int.lt_int.enabled tbms.var matches 0 run function default:int/lt_int/main
schedule clear default:int/lt_int/crash
scoreboard players set default.int.lt_int.crashCount tbms.var 0
execute unless score default.int.lt_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/305 1 append
