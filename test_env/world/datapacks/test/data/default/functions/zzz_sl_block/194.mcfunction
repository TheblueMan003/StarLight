# ==================================================
# void default.zzz_sl_block.194()
# a.k.a default.cmd.schedule.144.__lambda__
# ==================================================

schedule function default:int/neq_int_fail/crash 1 append
execute unless score default.int.neq_int_fail.enabled tbms.var matches 0 run function default:int/neq_int_fail/main
schedule clear default:int/neq_int_fail/crash
scoreboard players set default.int.neq_int_fail.crashCount tbms.var 0
execute unless score default.int.neq_int_fail.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/194 1 append
