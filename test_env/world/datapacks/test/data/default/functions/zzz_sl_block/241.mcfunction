# ==================================================
# void default.zzz_sl_block.241()
# a.k.a default.cmd.schedule.180.__lambda__
# ==================================================

schedule function default:int/eq_float/crash 1 append
execute unless score default.int.eq_float.enabled tbms.var matches 0 run function default:int/eq_float/main
schedule clear default:int/eq_float/crash
scoreboard players set default.int.eq_float.crashCount tbms.var 0
execute unless score default.int.eq_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/241 1 append
