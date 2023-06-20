scoreboard players set @s default.fruit.test.room.IN 0
function default:fruit/test/room/on-exit
execute if score default.fruit.test.room.counting tbms.var matches 0 run function default:zzz_sl_block/16
