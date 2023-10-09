# ==================================================
# void default.zzz_sl_block.170()
# a.k.a default.cmd.schedule.126.__lambda__
# ==================================================

schedule function default:int/not_addition_value/crash 1 append
execute unless score default.int.not_addition_value.enabled tbms.var matches 0 run function default:int/not_addition_value/main
schedule clear default:int/not_addition_value/crash
scoreboard players set default.int.not_addition_value.crashCount tbms.var 0
execute unless score default.int.not_addition_value.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/170 1 append
