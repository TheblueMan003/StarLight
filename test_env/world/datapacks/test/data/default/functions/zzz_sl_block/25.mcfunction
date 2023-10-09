# ==================================================
# void default.zzz_sl_block.25()
# a.k.a default.cmd.schedule.18.__lambda__
# ==================================================

schedule function default:int/tuple_unpacking_ints/crash 1 append
execute unless score default.int.tuple_unpacking_ints.enabled tbms.var matches 0 run function default:int/tuple_unpacking_ints/main
schedule clear default:int/tuple_unpacking_ints/crash
scoreboard players set default.int.tuple_unpacking_ints.crashCount tbms.var 0
execute unless score default.int.tuple_unpacking_ints.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/25 1 append
