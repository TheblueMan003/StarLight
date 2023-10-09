# ==================================================
# bool default.int.tuple_unpacking_floats.getResult-0()
# a.k.a default.int.tuple_unpacking_floats.getResult
# ==================================================

scoreboard players set default.int.tuple_unpacking_floats.getResult-0.tuple._0 tbms.var 3140
scoreboard players set default.int.tuple_unpacking_floats.getResult-0.tuple._1 tbms.var 2718
scoreboard players operation default.int.tuple_unpacking_floats.getResult-0.x tbms.var = default.int.tuple_unpacking_floats.getResult-0.tuple._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_floats.getResult-0.y tbms.var = default.int.tuple_unpacking_floats.getResult-0.tuple._1 tbms.var
scoreboard players set default.int.tuple_unpacking_floats.getResult-0._ret tbms.var 0
execute if score default.int.tuple_unpacking_floats.getResult-0.y tbms.var matches 2718 if score default.int.tuple_unpacking_floats.getResult-0.x tbms.var matches 3140 run scoreboard players set default.int.tuple_unpacking_floats.getResult-0._ret tbms.var 1
