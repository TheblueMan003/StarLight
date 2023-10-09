# ==================================================
# void default.zzz_sl_block.130()
# a.k.a default.cmd.schedule.96.__lambda__
# ==================================================

schedule function default:int/not_neq_int/crash 1 append
execute unless score default.int.not_neq_int.enabled tbms.var matches 0 run function default:int/not_neq_int/main
schedule clear default:int/not_neq_int/crash
scoreboard players set default.int.not_neq_int.crashCount tbms.var 0
execute unless score default.int.not_neq_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/130 1 append
