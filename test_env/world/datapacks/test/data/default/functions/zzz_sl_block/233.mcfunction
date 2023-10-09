# ==================================================
# void default.zzz_sl_block.233()
# a.k.a default.cmd.schedule.174.__lambda__
# ==================================================

schedule function default:int/neq_float/crash 1 append
execute unless score default.int.neq_float.enabled tbms.var matches 0 run function default:int/neq_float/main
schedule clear default:int/neq_float/crash
scoreboard players set default.int.neq_float.crashCount tbms.var 0
execute unless score default.int.neq_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/233 1 append
