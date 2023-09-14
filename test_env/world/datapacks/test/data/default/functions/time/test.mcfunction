# ==================================================
# void default.time.test()
# ==================================================

execute if score default.time.test._0.a.tick tbms.var matches 74460 run say hi
scoreboard players operation default.time.test._0._2.tick tbms.var = default.time.test._0.a.tick tbms.var
execute if score default.time.test._0._2.tick tbms.var matches 24000 run say 20
execute if score default.time.test._0._2.tick tbms.var matches 36000 run say 30
scoreboard players set default.time.test._0._5.tick tbms.var 20
scoreboard players operation default.time.test._0._5.tick tbms.var *= c2 tbms.const
execute if score default.time.test._0._5.tick tbms.var = default.time.test._0._2.tick tbms.var run say 2
