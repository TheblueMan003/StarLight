# ==================================================
# void default.zzz_sl_block.109()
# a.k.a default.cmd.schedule.0.__lambda__
# ==================================================

schedule function default:array/sugar/crash 1 append
execute unless score default.array.sugar.enabled tbms.var matches 0 run function default:array/sugar/main
schedule clear default:array/sugar/crash
scoreboard players set default.array.sugar.crashCount tbms.var 0
execute unless score default.array.sugar.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/109 1 append
