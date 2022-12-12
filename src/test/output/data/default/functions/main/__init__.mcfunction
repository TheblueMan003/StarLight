execute store result score default.main._0 tbms.var run data get entity @s "Path[0]" 1000
scoreboard players set default.main._2 tbms.var 10
execute if score default.main._0 tbms.var > default.main._2 tbms.var run say hi
execute store result entity @s "Path[0]" "double" 0.001 run scoreboard players get 10 tbms.const
scoreboard players set default.main.a tbms.var 2
scoreboard players operation default.main._4 tbms.var = default.main.a tbms.var
scoreboard players operation default.main._4 tbms.var *= 5 tbms.const
scoreboard players operation default.main.a tbms.var = default.main._4 tbms.var
