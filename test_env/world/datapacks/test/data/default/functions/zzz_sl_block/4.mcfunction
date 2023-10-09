# ==================================================
# void default.zzz_sl_block.4()
# a.k.a default.cmd.schedule.0.__lambda__
# ==================================================

schedule function default:int/tuple_unpacking_nested_tuples/crash 1 append
execute unless score default.int.tuple_unpacking_nested_tuples.enabled tbms.var matches 0 run function default:int/tuple_unpacking_nested_tuples/main
schedule clear default:int/tuple_unpacking_nested_tuples/crash
scoreboard players set default.int.tuple_unpacking_nested_tuples.crashCount tbms.var 0
execute unless score default.int.tuple_unpacking_nested_tuples.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/4 1 append
