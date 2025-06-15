# ==================================================
# void default.zzz_sl_block.189()
# a.k.a default.cmd.schedule.12.__lambda__
# ==================================================

schedule function default:array/initer/crash 1 append
execute unless score default.array.initer.enabled tbms.var matches 0 run function default:array/initer/main
schedule clear default:array/initer/crash
scoreboard players set default.array.initer.crashCount tbms.var 0
execute unless score default.array.initer.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/189 1 append
