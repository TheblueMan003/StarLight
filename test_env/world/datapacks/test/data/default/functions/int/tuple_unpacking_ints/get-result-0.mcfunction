# ==================================================
# bool default.int.tuple_unpacking_ints.getResult-0()
# a.k.a default.int.tuple_unpacking_ints.getResult
# ==================================================

scoreboard players set default.int.tuple_unpacking_ints.getResult-0.tuple._0 tbms.var 5
scoreboard players set default.int.tuple_unpacking_ints.getResult-0.tuple._1 tbms.var 10
scoreboard players operation default.int.tuple_unpacking_ints.getResult-0.a tbms.var = default.int.tuple_unpacking_ints.getResult-0.tuple._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_ints.getResult-0.b tbms.var = default.int.tuple_unpacking_ints.getResult-0.tuple._1 tbms.var
scoreboard players set default.int.tuple_unpacking_ints.getResult-0._ret tbms.var 0
execute if score default.int.tuple_unpacking_ints.getResult-0.b tbms.var matches 10 if score default.int.tuple_unpacking_ints.getResult-0.a tbms.var matches 5 run scoreboard players set default.int.tuple_unpacking_ints.getResult-0._ret tbms.var 1
