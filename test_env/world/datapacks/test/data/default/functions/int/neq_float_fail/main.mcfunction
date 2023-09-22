# ==================================================
# void default.int.neq_float_fail.main()
# ==================================================

scoreboard players add default.int.neq_float_fail.time tbms.var 1
scoreboard players set default.int.neq_float_fail.main._0 tbms.var 2
execute if score default.int.neq_float_fail.time tbms.var >= default.int.neq_float_fail.main._0 tbms.var unless score default.int.neq_float_fail.enabled tbms.var matches 0 run function default:zzz_sl_block/201
