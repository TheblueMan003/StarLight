# ==================================================
# void default.zzz_sl_block.329()
# a.k.a default.cmd.schedule.246.__lambda__
# ==================================================

schedule function default:int/multi_variable/crash 1 append
execute unless score default.int.multi_variable.enabled tbms.var matches 0 run function default:int/multi_variable/main
schedule clear default:int/multi_variable/crash
scoreboard players set default.int.multi_variable.crashCount tbms.var 0
execute unless score default.int.multi_variable.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/329 1 append
