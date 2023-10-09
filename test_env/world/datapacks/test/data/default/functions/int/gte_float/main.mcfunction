# ==================================================
# void default.int.gte_float.main()
# ==================================================

scoreboard players add default.int.gte_float.time tbms.var 1
scoreboard players set default.int.gte_float.main._0 tbms.var 2
execute if score default.int.gte_float.time tbms.var >= default.int.gte_float.main._0 tbms.var unless score default.int.gte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/216
