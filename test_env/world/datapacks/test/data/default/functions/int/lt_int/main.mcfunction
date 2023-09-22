# ==================================================
# void default.int.lt_int.main()
# ==================================================

scoreboard players add default.int.lt_int.time tbms.var 1
scoreboard players set default.int.lt_int.main._0 tbms.var 2
execute if score default.int.lt_int.time tbms.var >= default.int.lt_int.main._0 tbms.var unless score default.int.lt_int.enabled tbms.var matches 0 run function default:zzz_sl_block/304
