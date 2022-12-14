execute unless score default.enumComparaison.a tbms.var = default.enumComparaison.a tbms.var run scoreboard players set default.enumComparaison.a tbms.var 0
execute unless score default.enumComparaison.b tbms.var = default.enumComparaison.b tbms.var run scoreboard players set default.enumComparaison.b tbms.var 0
execute if score default.enumComparaison.a tbms.var = default.enumComparaison.b tbms.var run say hi
execute if score default.enumComparaison.a tbms.var = default.enumComparaison.a tbms.var run say hi
execute if score default.enumComparaison.a tbms.var >= default.enumComparaison.b tbms.var run say hi
execute if score default.enumComparaison.a tbms.var matches 0 run say hi
execute unless score default.enumComparaison.a tbms.var matches 0 run say hi
execute if score default.enumComparaison.a tbms.var matches 0.. run say hi
