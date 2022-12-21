scoreboard players set default.a.test.main-0._2 tbms.var 1
execute unless score default.a.test.counting tbms.var matches 0 run scoreboard players add default.a.test.nbPlayer tbms.var 1
scoreboard players set default.a.test.main-0._3 tbms.var 0
execute unless score @s s-2042271694 matches 0 run function default/zzz_sl_block/0
execute if score default.a.test.main-0._3 tbms.var matches 0 run function default/zzz_sl_block/1
