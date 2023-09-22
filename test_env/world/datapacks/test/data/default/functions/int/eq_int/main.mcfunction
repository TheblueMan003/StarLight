# ==================================================
# void default.int.eq_int.main()
# ==================================================

scoreboard players add default.int.eq_int.time tbms.var 1
scoreboard players set default.int.eq_int.main._0 tbms.var 2
execute if score default.int.eq_int.time tbms.var >= default.int.eq_int.main._0 tbms.var unless score default.int.eq_int.enabled tbms.var matches 0 run function default:zzz_sl_block/288
