# ==================================================
# void default.array.sugar.main()
# ==================================================

scoreboard players add default.array.sugar.time tbms.var 1
scoreboard players set default.array.sugar.main._0 tbms.var 2
execute if score default.array.sugar.time tbms.var >= default.array.sugar.main._0 tbms.var unless score default.array.sugar.enabled tbms.var matches 0 run function default:zzz_sl_block/108
