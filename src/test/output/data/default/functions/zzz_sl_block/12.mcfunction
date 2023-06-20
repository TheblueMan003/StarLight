function default:fruit/test/room/on-enter
execute if score default.fruit.test.room.nbPlayer tbms.var matches 0 run function default:fruit/test/room/on-activate
execute if score default.fruit.test.room.counting tbms.var matches 0 run scoreboard players add default.fruit.test.room.nbPlayer tbms.var 1
scoreboard players set @s default.fruit.test.room.IN 1
