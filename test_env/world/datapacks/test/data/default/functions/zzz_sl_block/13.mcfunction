# ==================================================
# void default.zzz_sl_block.13()
# a.k.a default.int.tuple_unpacking_mixed_types.start.1
# ==================================================

scoreboard players set default.int.tuple_unpacking_mixed_types.enabled tbms.var 1
scoreboard players set default.int.tuple_unpacking_mixed_types.time tbms.var 0
execute unless score default.int.tuple_unpacking_mixed_types.enabled tbms.var matches 0 run function default:zzz_sl_block/11
