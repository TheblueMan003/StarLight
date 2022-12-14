execute unless score default.intComparaison.a tbms.var = default.intComparaison.a tbms.var run scoreboard players set default.intComparaison.a tbms.var 0
execute unless score default.intComparaison.b tbms.var = default.intComparaison.b tbms.var run scoreboard players set default.intComparaison.b tbms.var 0
execute if score default.intComparaison.a tbms.var = default.intComparaison.b tbms.var run say hi
execute if score default.intComparaison.a tbms.var = default.intComparaison.a tbms.var run say hi
execute if score default.intComparaison.a tbms.var >= default.intComparaison.b tbms.var run say hi
execute if score default.intComparaison.a tbms.var matches 0 run say hi
execute unless score default.intComparaison.a tbms.var matches 0 run say hi
execute if score default.intComparaison.a tbms.var matches 0.. run say hi
execute if score default.intComparaison.b tbms.var matches 0 run say hi
execute unless score default.intComparaison.b tbms.var matches 0 run say hi
execute if score default.intComparaison.b tbms.var matches ..-1 run say hi
