# ==================================================
# void default.zzz_sl_block.11()
# a.k.a default.cmd.schedule.6.__lambda__
# ==================================================

schedule function default:int/tuple_unpacking_mixed_types/crash 1 append
execute unless score default.int.tuple_unpacking_mixed_types.enabled tbms.var matches 0 run function default:int/tuple_unpacking_mixed_types/main
schedule clear default:int/tuple_unpacking_mixed_types/crash
scoreboard players set default.int.tuple_unpacking_mixed_types.crashCount tbms.var 0
execute unless score default.int.tuple_unpacking_mixed_types.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/11 1 append
