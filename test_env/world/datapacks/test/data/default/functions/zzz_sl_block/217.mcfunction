# ==================================================
# void default.zzz_sl_block.217()
# a.k.a default.cmd.schedule.162.__lambda__
# ==================================================

schedule function default:int/gte_float/crash 1 append
execute unless score default.int.gte_float.enabled tbms.var matches 0 run function default:int/gte_float/main
schedule clear default:int/gte_float/crash
scoreboard players set default.int.gte_float.crashCount tbms.var 0
execute unless score default.int.gte_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/217 1 append
