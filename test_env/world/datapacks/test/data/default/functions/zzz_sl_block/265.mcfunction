# ==================================================
# void default.zzz_sl_block.265()
# a.k.a default.cmd.schedule.198.__lambda__
# ==================================================

schedule function default:int/gte_int/crash 1 append
execute unless score default.int.gte_int.enabled tbms.var matches 0 run function default:int/gte_int/main
schedule clear default:int/gte_int/crash
scoreboard players set default.int.gte_int.crashCount tbms.var 0
execute unless score default.int.gte_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/265 1 append
