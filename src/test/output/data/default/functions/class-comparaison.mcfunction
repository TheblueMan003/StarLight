execute as @e[tag=__class__] at @s if score default.classComparaison.a tbms.var = @s s1644059425 run function default/zzz_sl_block/0
execute unless score default.classComparaison.e.__eq__._ret tbms.var matches 0 run say hi
execute as @e[tag=__class__] at @s if score default.classComparaison.a tbms.var = @s s1644059425 run function default/zzz_sl_block/1
execute unless score default.classComparaison.e.__ne__._ret tbms.var matches 0 run say hi
execute as @e[tag=__class__] at @s if score default.classComparaison.a tbms.var = @s s1644059425 run function default/zzz_sl_block/2
execute unless score default.classComparaison.e.__ge__._ret tbms.var matches 0 run say hi
execute as @e[tag=__class__] at @s if score default.classComparaison.a tbms.var = @s s1644059425 run function default/zzz_sl_block/3
execute unless score default.classComparaison.e.__gt__._ret tbms.var matches 0 run say hi
execute as @e[tag=__class__] at @s if score default.classComparaison.a tbms.var = @s s1644059425 run function default/zzz_sl_block/4
execute unless score default.classComparaison.e.__lt__._ret tbms.var matches 0 run say hi
execute as @e[tag=__class__] at @s if score default.classComparaison.a tbms.var = @s s1644059425 run function default/zzz_sl_block/5
execute unless score default.classComparaison.e.__le__._ret tbms.var matches 0 run say hi
