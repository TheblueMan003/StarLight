# ==================================================
# void default.zzz_sl_block.18()
# a.k.a default.cmd.schedule.12.__lambda__
# ==================================================

schedule function default:int/tuple_unpacking_floats/crash 1 append
execute unless score default.int.tuple_unpacking_floats.enabled tbms.var matches 0 run function default:int/tuple_unpacking_floats/main
schedule clear default:int/tuple_unpacking_floats/crash
scoreboard players set default.int.tuple_unpacking_floats.crashCount tbms.var 0
execute unless score default.int.tuple_unpacking_floats.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/18 1 append
