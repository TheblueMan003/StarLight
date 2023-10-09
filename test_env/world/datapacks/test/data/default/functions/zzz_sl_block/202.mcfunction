# ==================================================
# void default.zzz_sl_block.202()
# a.k.a default.cmd.schedule.150.__lambda__
# ==================================================

schedule function default:int/neq_float_fail/crash 1 append
execute unless score default.int.neq_float_fail.enabled tbms.var matches 0 run function default:int/neq_float_fail/main
schedule clear default:int/neq_float_fail/crash
scoreboard players set default.int.neq_float_fail.crashCount tbms.var 0
execute unless score default.int.neq_float_fail.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/202 1 append
