# ==================================================
# void default.zzz_sl_block.289()
# a.k.a default.cmd.schedule.216.__lambda__
# ==================================================

schedule function default:int/eq_int/crash 1 append
execute unless score default.int.eq_int.enabled tbms.var matches 0 run function default:int/eq_int/main
schedule clear default:int/eq_int/crash
scoreboard players set default.int.eq_int.crashCount tbms.var 0
execute unless score default.int.eq_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/289 1 append
