scoreboard players operation default.test.A.damage.dmg.type tbms.var = default.test.test.dmg.type tbms.var
scoreboard players operation default.test.A.damage.dmg.amount tbms.var = default.test.test.dmg.amount tbms.var
scoreboard players operation default.test.A.last.set.index tbms.var = @s default.test.A.hp
scoreboard players set default.test.A.last.set.value tbms.var 1
scoreboard players operation default.test.A.last.set._0 tbms.var = default.test.A.last.set.index tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 0 run scoreboard players operation @s default.test.A.last.0 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 1 run scoreboard players operation @s default.test.A.last.1 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 2 run scoreboard players operation @s default.test.A.last.2 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 3 run scoreboard players operation @s default.test.A.last.3 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 4 run scoreboard players operation @s default.test.A.last.4 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 5 run scoreboard players operation @s default.test.A.last.5 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 6 run scoreboard players operation @s default.test.A.last.6 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 7 run scoreboard players operation @s default.test.A.last.7 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 8 run scoreboard players operation @s default.test.A.last.8 = default.test.A.last.set.value tbms.var
execute if score default.test.A.last.set._0 tbms.var matches 9 run scoreboard players operation @s default.test.A.last.9 = default.test.A.last.set.value tbms.var
execute if score default.test.A.damage.dmg.type tbms.var matches 0 run function default:zzz_sl_block/4
scoreboard players operation @s default.test.A.hp -= default.test.A.damage.dmg.amount tbms.var
