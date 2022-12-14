execute unless score default.floatComparaison.a tbms.var = default.floatComparaison.a tbms.var run scoreboard players set default.floatComparaison.a tbms.var 0
execute unless score default.floatComparaison.b tbms.var = default.floatComparaison.b tbms.var run scoreboard players set default.floatComparaison.b tbms.var 0
execute if score default.floatComparaison.a tbms.var = default.floatComparaison.b tbms.var run say hi
execute if score default.floatComparaison.a tbms.var = default.floatComparaison.a tbms.var run say hi
execute if score default.floatComparaison.a tbms.var >= default.floatComparaison.b tbms.var run say hi
execute if score default.floatComparaison.a tbms.var matches 0 run say hi
execute unless score default.floatComparaison.a tbms.var matches 0 run say hi
execute if score default.floatComparaison.a tbms.var matches 0.. run say hi
execute if score default.floatComparaison.b tbms.var matches 0 run say hi
execute unless score default.floatComparaison.b tbms.var matches 0 run say hi
execute if score default.floatComparaison.b tbms.var matches ..-1 run say hi
