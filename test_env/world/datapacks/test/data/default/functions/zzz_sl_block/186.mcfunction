# ==================================================
# void default.zzz_sl_block.186()
# a.k.a default.cmd.schedule.138.__lambda__
# ==================================================

schedule function default:int/not_addition_variable/crash 1 append
execute unless score default.int.not_addition_variable.enabled tbms.var matches 0 run function default:int/not_addition_variable/main
schedule clear default:int/not_addition_variable/crash
scoreboard players set default.int.not_addition_variable.crashCount tbms.var 0
execute unless score default.int.not_addition_variable.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/186 1 append
