# ==================================================
# void default.int.multi_variable.main()
# ==================================================

scoreboard players add default.int.multi_variable.time tbms.var 1
scoreboard players set default.int.multi_variable.main._0 tbms.var 2
execute if score default.int.multi_variable.time tbms.var >= default.int.multi_variable.main._0 tbms.var unless score default.int.multi_variable.enabled tbms.var matches 0 run function default:zzz_sl_block/328
