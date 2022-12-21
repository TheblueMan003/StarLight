function default/a/test/on-enter-0
execute if score default.a.test.nbPlayer tbms.var matches 0 run function default/a/test/on-activate-0
execute if score default.a.test.counting tbms.var matches 0 run scoreboard players add default.a.test.nbPlayer tbms.var 1
scoreboard players set @s s-2042271694 1
