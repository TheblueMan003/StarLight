# ==================================================
# void default.test.test()
# ==================================================

scoreboard players set default.test.test.a tbms.var 0
scoreboard players set default.test.test._0 tbms.var 0
execute unless score default.test.test.a tbms.var matches 0 run function default:zzz_sl_block/0
execute if score default.test.test._0 tbms.var matches 0 run say bye
