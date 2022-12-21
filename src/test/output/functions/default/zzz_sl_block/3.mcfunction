scoreboard players remove default.a.test.nbPlayer tbms.var 1
execute if score default.a.test.nbPlayer tbms.var matches 0 run function default/a/test/on-disactivate-0
execute if score default.a.test.nbPlayer tbms.var matches ..-1 run scoreboard players set default.a.test.nbPlayer tbms.var 0
