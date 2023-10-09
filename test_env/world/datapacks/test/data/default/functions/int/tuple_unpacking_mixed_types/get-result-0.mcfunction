# ==================================================
# bool default.int.tuple_unpacking_mixed_types.getResult-0()
# a.k.a default.int.tuple_unpacking_mixed_types.getResult
# ==================================================

scoreboard players set default.int.tuple_unpacking_mixed_types.getResult-0.tuple._0 tbms.var 42
scoreboard players set default.int.tuple_unpacking_mixed_types.getResult-0.tuple._1 tbms.var 3140
scoreboard players set default.int.tuple_unpacking_mixed_types.getResult-0.a tbms.var 0
scoreboard players set default.int.tuple_unpacking_mixed_types.getResult-0.b tbms.var 0
scoreboard players operation default.int.tuple_unpacking_mixed_types.getResult-0.a tbms.var = default.int.tuple_unpacking_mixed_types.getResult-0.tuple._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_mixed_types.getResult-0.b tbms.var = default.int.tuple_unpacking_mixed_types.getResult-0.tuple._1 tbms.var
scoreboard players set default.int.tuple_unpacking_mixed_types.getResult-0._ret tbms.var 0
execute if score default.int.tuple_unpacking_mixed_types.getResult-0.b tbms.var matches 3140 if score default.int.tuple_unpacking_mixed_types.getResult-0.a tbms.var matches 42 run scoreboard players set default.int.tuple_unpacking_mixed_types.getResult-0._ret tbms.var 1
