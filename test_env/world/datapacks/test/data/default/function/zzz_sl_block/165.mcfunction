# ==================================================
# void default.zzz_sl_block.165()
# a.k.a default.cmd.schedule.6.__lambda__
# ==================================================

schedule function default:array/addition/crash 1 append
execute unless score default.array.addition.enabled tbms.var matches 0 run function default:array/addition/main
schedule clear default:array/addition/crash
scoreboard players set default.array.addition.crashCount tbms.var 0
execute unless score default.array.addition.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/165 1 append
