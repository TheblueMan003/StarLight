tag @e[tag=default.fruit.test.objects] remove default.fruit.test.objects
summon minecraft:cow ~ ~ ~ {Tags:["default.fruit.test.objects"]}
scoreboard players add default.fruit.test.objects.binding tbms.var 0
execute if score default.fruit.test.objects.isPlayer tbms.var matches 1 as @a[tag=default.fruit.test.objects] at @s run function default:zzz_sl_block/0
execute if score default.fruit.test.objects.isPlayer tbms.var matches 0 as @e[tag=default.fruit.test.objects] at @s run function default:zzz_sl_block/1
scoreboard players operation default.fruit.test.objects.isPlayer tbms.var += default.fruit.test.onStart-0.0._1.isPlayer tbms.var
scoreboard players operation default.fruit.test.objects.binding tbms.var += default.fruit.test.onStart-0.0._1.binding tbms.var
scoreboard players set default.fruit.test.onStart-0.0._2 tbms.var 0
execute unless score default.fruit.test.objects.isPlayer tbms.var matches 0 run function default:zzz_sl_block/3
execute if score default.fruit.test.onStart-0.0._2 tbms.var matches 0 run tag @e[tag=default.fruit.test.onStart-0.0._1] add default.fruit.test.objects
