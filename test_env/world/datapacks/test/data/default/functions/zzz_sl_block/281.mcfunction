# ==================================================
# void default.zzz_sl_block.281()
# a.k.a default.cmd.schedule.210.__lambda__
# ==================================================

schedule function default:int/neq_int/crash 1 append
execute unless score default.int.neq_int.enabled tbms.var matches 0 run function default:int/neq_int/main
schedule clear default:int/neq_int/crash
scoreboard players set default.int.neq_int.crashCount tbms.var 0
execute unless score default.int.neq_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/281 1 append
