# ==================================================
# void default.zzz_sl_block.27()
# a.k.a default.int.tuple_unpacking_ints.start.1
# ==================================================

scoreboard players set default.int.tuple_unpacking_ints.enabled tbms.var 1
scoreboard players set default.int.tuple_unpacking_ints.time tbms.var 0
execute unless score default.int.tuple_unpacking_ints.enabled tbms.var matches 0 run function default:zzz_sl_block/25
