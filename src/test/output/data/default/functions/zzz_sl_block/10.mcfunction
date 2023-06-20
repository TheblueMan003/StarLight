scoreboard players set default.fruit.test.room.__main_player__._3 tbms.var 1
function default:fruit/test/room/on-stay
execute if entity @s[gamemode=creative] if score default.fruit.test.room.hasDisplay tbms.var matches 0 unless score default.fruit.test.room.display tbms.var matches 0 run function default:zzz_sl_block/8
