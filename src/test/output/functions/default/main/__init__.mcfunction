scoreboard players set default.main._1 tbms.var 0
execute unless score default.main.lol.isPlayer tbms.var matches 0 run function default/zzz_sl_block/0
execute if score default.main._1 tbms.var matches 0 run tag @e[tag=default.main.lol] remove default.main.lol
scoreboard players set default.main.lol.isPlayer tbms.var 0
tag @s add default.main.lol
tag @s[tag=!default.main.lol] add default.main.test
