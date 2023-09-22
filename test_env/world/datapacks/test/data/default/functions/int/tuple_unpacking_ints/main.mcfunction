# ==================================================
# void default.int.tuple_unpacking_ints.main()
# ==================================================

scoreboard players add default.int.tuple_unpacking_ints.time tbms.var 1
scoreboard players set default.int.tuple_unpacking_ints.main._0 tbms.var 2
execute if score default.int.tuple_unpacking_ints.time tbms.var >= default.int.tuple_unpacking_ints.main._0 tbms.var unless score default.int.tuple_unpacking_ints.enabled tbms.var matches 0 run function default:zzz_sl_block/24
