# ==================================================
# void default.array.addition.main()
# ==================================================

scoreboard players add default.array.addition.time tbms.var 1
scoreboard players set default.array.addition.main._0 tbms.var 2
execute if score default.array.addition.time tbms.var >= default.array.addition.main._0 tbms.var unless score default.array.addition.enabled tbms.var matches 0 run function default:zzz_sl_block/164
