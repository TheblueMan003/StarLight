scoreboard players set default.fruit.test.room.__main_player__._2 tbms.var 1
execute unless score default.fruit.test.room.counting tbms.var matches 0 run scoreboard players add default.fruit.test.room.nbPlayer tbms.var 1
scoreboard players set default.fruit.test.room.__main_player__._3 tbms.var 0
execute unless score @s default.fruit.test.room.IN matches 0 run function default:zzz_sl_block/10
execute if score default.fruit.test.room.__main_player__._3 tbms.var matches 0 run function default:zzz_sl_block/12
