# ==================================================
# void default.int.tuple_unpacking_nested_tuples.onStop-0()
# a.k.a default.int.tuple_unpacking_nested_tuples.onStop
# ==================================================

scoreboard players set default.int.tuple_unpacking_nested_tuples.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._0._0 tbms.var 1
scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._0._1 tbms.var 2
scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._1._0 tbms.var 3140
scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._1._1 tbms.var 2718
execute unless score default.int.tuple_unpacking_nested_tuples.getResult-0.a tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.a tbms.var run scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.a tbms.var 0
execute unless score default.int.tuple_unpacking_nested_tuples.getResult-0.b tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.b tbms.var run scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.b tbms.var 0
execute unless score default.int.tuple_unpacking_nested_tuples.getResult-0.x tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.x tbms.var run scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.x tbms.var 0
execute unless score default.int.tuple_unpacking_nested_tuples.getResult-0.y tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.y tbms.var run scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0.y tbms.var 0
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.c._0 tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._0._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.c._1 tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._0._1 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.d._0 tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._1._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.d._1 tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.tuple._1._1 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.a tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.c._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.b tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.c._1 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.x tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.d._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_nested_tuples.getResult-0.y tbms.var = default.int.tuple_unpacking_nested_tuples.getResult-0.d._1 tbms.var
scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0._ret tbms.var 0
execute if score default.int.tuple_unpacking_nested_tuples.getResult-0.y tbms.var matches 2718 if score default.int.tuple_unpacking_nested_tuples.getResult-0.x tbms.var matches 3140 if score default.int.tuple_unpacking_nested_tuples.getResult-0.b tbms.var matches 2 if score default.int.tuple_unpacking_nested_tuples.getResult-0.a tbms.var matches 1 run scoreboard players set default.int.tuple_unpacking_nested_tuples.getResult-0._ret tbms.var 1
execute unless score default.int.tuple_unpacking_nested_tuples.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/0
execute if score default.int.tuple_unpacking_nested_tuples.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/1
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
