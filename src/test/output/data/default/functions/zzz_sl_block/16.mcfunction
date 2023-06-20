scoreboard players remove default.fruit.test.room.nbPlayer tbms.var 1
execute if score default.fruit.test.room.nbPlayer tbms.var matches 0 run function default:fruit/test/room/on-desactivate
execute if score default.fruit.test.room.nbPlayer tbms.var matches ..-1 run scoreboard players set default.fruit.test.room.nbPlayer tbms.var 0
