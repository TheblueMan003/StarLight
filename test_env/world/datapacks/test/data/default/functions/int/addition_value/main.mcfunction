# ==================================================
# void default.int.addition_value.main()
# ==================================================

scoreboard players add default.int.addition_value.time tbms.var 1
scoreboard players set default.int.addition_value.main._0 tbms.var 2
execute if score default.int.addition_value.time tbms.var >= default.int.addition_value.main._0 tbms.var unless score default.int.addition_value.enabled tbms.var matches 0 run function default:zzz_sl_block/320
