# ==================================================
# void default.int.not_gte_float.main()
# ==================================================

scoreboard players add default.int.not_gte_float.time tbms.var 1
scoreboard players set default.int.not_gte_float.main._0 tbms.var 2
execute if score default.int.not_gte_float.time tbms.var >= default.int.not_gte_float.main._0 tbms.var unless score default.int.not_gte_float.enabled tbms.var matches 0 run function default:zzz_sl_block/65
