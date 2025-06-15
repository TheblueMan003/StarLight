# ==================================================
# void default.array.initer.main()
# ==================================================

scoreboard players add default.array.initer.time tbms.var 1
scoreboard players set default.array.initer.main._0 tbms.var 2
execute if score default.array.initer.time tbms.var >= default.array.initer.main._0 tbms.var unless score default.array.initer.enabled tbms.var matches 0 run function default:zzz_sl_block/188
