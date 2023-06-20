execute if score default.fruit.test.room.nbPlayer tbms.var matches 1.. run function default:zzz_sl_block/6
scoreboard players set default.fruit.test.room.__main_player__._2 tbms.var 0
scoreboard players set default.fruit.test.room.__main_player__.1.__hasFunctionReturned__ tbms.var 0
scoreboard players set default.fruit.test.room.__main_player__.1.__hasFunctionReturned__ tbms.var 1
execute if score default.game.Room.z tbms.var matches ..10 if score default.game.Room.z tbms.var matches 0.. if score default.game.Room.y tbms.var matches ..10 if score default.game.Room.y tbms.var matches 0.. if score default.game.Room.x tbms.var matches ..10 if score default.game.Room.x tbms.var matches 0.. run function default:zzz_sl_block/14
execute unless score @s default.fruit.test.room.IN matches 0 if score default.fruit.test.room.__main_player__._2 tbms.var matches 0 run function default:zzz_sl_block/18
