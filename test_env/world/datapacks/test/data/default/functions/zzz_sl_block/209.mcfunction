# ==================================================
# void default.zzz_sl_block.209()
# a.k.a default.cmd.schedule.156.__lambda__
# ==================================================

schedule function default:int/neq_float_delta/crash 1 append
execute unless score default.int.neq_float_delta.enabled tbms.var matches 0 run function default:int/neq_float_delta/main
schedule clear default:int/neq_float_delta/crash
scoreboard players set default.int.neq_float_delta.crashCount tbms.var 0
execute unless score default.int.neq_float_delta.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/209 1 append
