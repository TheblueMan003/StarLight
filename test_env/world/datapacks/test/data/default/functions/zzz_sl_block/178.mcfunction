# ==================================================
# void default.zzz_sl_block.178()
# a.k.a default.cmd.schedule.132.__lambda__
# ==================================================

schedule function default:int/not_multi_variable/crash 1 append
execute unless score default.int.not_multi_variable.enabled tbms.var matches 0 run function default:int/not_multi_variable/main
schedule clear default:int/not_multi_variable/crash
scoreboard players set default.int.not_multi_variable.crashCount tbms.var 0
execute unless score default.int.not_multi_variable.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/178 1 append
