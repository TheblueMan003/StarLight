function default:fruit/test/on-stop
execute if score default.fruit.test.objects.isPlayer tbms.var matches 1 as @a[tag=default.fruit.test.objects] at @s run function default:zzz_sl_block/22
execute if score default.fruit.test.objects.isPlayer tbms.var matches 0 as @e[tag=default.fruit.test.objects] at @s run function default:zzz_sl_block/24
