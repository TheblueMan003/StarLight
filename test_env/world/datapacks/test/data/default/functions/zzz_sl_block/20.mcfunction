# ==================================================
# void default.zzz_sl_block.20()
# a.k.a default.int.tuple_unpacking_floats.start.1
# ==================================================

scoreboard players set default.int.tuple_unpacking_floats.enabled tbms.var 1
scoreboard players set default.int.tuple_unpacking_floats.time tbms.var 0
execute unless score default.int.tuple_unpacking_floats.enabled tbms.var matches 0 run function default:zzz_sl_block/18
