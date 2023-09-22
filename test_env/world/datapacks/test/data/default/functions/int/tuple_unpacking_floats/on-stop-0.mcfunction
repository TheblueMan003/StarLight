# ==================================================
# void default.int.tuple_unpacking_floats.onStop-0()
# a.k.a default.int.tuple_unpacking_floats.onStop
# ==================================================

scoreboard players set default.int.tuple_unpacking_floats.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.tuple_unpacking_floats.getResult-0.tuple._0 tbms.var 3140
scoreboard players set default.int.tuple_unpacking_floats.getResult-0.tuple._1 tbms.var 2718
scoreboard players operation default.int.tuple_unpacking_floats.getResult-0.x tbms.var = default.int.tuple_unpacking_floats.getResult-0.tuple._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_floats.getResult-0.y tbms.var = default.int.tuple_unpacking_floats.getResult-0.tuple._1 tbms.var
scoreboard players set default.int.tuple_unpacking_floats.getResult-0._ret tbms.var 0
execute if score default.int.tuple_unpacking_floats.getResult-0.y tbms.var matches 2718 if score default.int.tuple_unpacking_floats.getResult-0.x tbms.var matches 3140 run scoreboard players set default.int.tuple_unpacking_floats.getResult-0._ret tbms.var 1
execute unless score default.int.tuple_unpacking_floats.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/15
execute if score default.int.tuple_unpacking_floats.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/16
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
