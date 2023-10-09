# ==================================================
# void default.zzz_sl_block.337()
# a.k.a default.cmd.schedule.252.__lambda__
# ==================================================

schedule function default:int/addition_variable/crash 1 append
execute unless score default.int.addition_variable.enabled tbms.var matches 0 run function default:int/addition_variable/main
schedule clear default:int/addition_variable/crash
scoreboard players set default.int.addition_variable.crashCount tbms.var 0
execute unless score default.int.addition_variable.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/337 1 append
