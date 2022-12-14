execute unless score default.boolComparaison.a tbms.var = default.boolComparaison.a tbms.var run scoreboard players set 0
execute unless score default.boolComparaison.b tbms.var = default.boolComparaison.b tbms.var run scoreboard players set 0
execute if score default.boolComparaison.a tbms.var = default.boolComparaison.b tbms.var run say hi
execute if score default.boolComparaison.a tbms.var = default.boolComparaison.a tbms.var run say hi
execute if score default.boolComparaison.a tbms.var >= default.boolComparaison.b tbms.var run say hi
execute if score default.boolComparaison.a tbms.var matches 1 run say hi
execute unless score default.boolComparaison.a tbms.var matches 0 run say hi
execute if score default.boolComparaison.a tbms.var matches 1.. run say hi
execute if score default.boolComparaison.b tbms.var matches 1 run say hi
execute unless score default.boolComparaison.b tbms.var matches 1 run say hi
execute if score default.boolComparaison.b tbms.var matches ..-1 run say hi
