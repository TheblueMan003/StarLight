scoreboard players set default.fruit.test.room.particule.__hasFunctionReturned__ tbms.var 0
scoreboard players operation default.fruit.test.room.particule._0 tbms.var = default.fruit.test.room.color tbms.var
execute if score default.fruit.test.room.particule._0 tbms.var matches 1 run particle minecraft:dust 1 0 0 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 2 run particle minecraft:dust 1 0.5 0 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 3 run particle minecraft:dust 1 1 0 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 4 run particle minecraft:dust 0 1 0 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 5 run particle minecraft:dust 0 1 1 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 6 run particle minecraft:dust 0 0 1 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 7 run particle minecraft:dust 1 0 1 1 ~ ~ ~ 0 0 0 0 1
execute if score default.fruit.test.room.particule._0 tbms.var matches 0 run particle minecraft:dust 1 1 1 1 ~ ~ ~ 0 0 0 0 1
