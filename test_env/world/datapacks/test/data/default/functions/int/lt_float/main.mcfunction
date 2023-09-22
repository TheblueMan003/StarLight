# ==================================================
# void default.int.lt_float.main()
# ==================================================

scoreboard players add default.int.lt_float.time tbms.var 1
scoreboard players set default.int.lt_float.main._0 tbms.var 2
execute if score default.int.lt_float.time tbms.var >= default.int.lt_float.main._0 tbms.var unless score default.int.lt_float.enabled tbms.var matches 0 run function default:zzz_sl_block/256
