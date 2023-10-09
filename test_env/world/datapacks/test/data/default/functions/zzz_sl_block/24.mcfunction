# ==================================================
# void default.zzz_sl_block.24()
# a.k.a default.int.tuple_unpacking_ints.stop.1
# ==================================================

scoreboard players set default.int.tuple_unpacking_ints.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.tuple_unpacking_ints.getResult-0.tuple._0 tbms.var 5
scoreboard players set default.int.tuple_unpacking_ints.getResult-0.tuple._1 tbms.var 10
scoreboard players operation default.int.tuple_unpacking_ints.getResult-0.a tbms.var = default.int.tuple_unpacking_ints.getResult-0.tuple._0 tbms.var
scoreboard players operation default.int.tuple_unpacking_ints.getResult-0.b tbms.var = default.int.tuple_unpacking_ints.getResult-0.tuple._1 tbms.var
scoreboard players set default.int.tuple_unpacking_ints.getResult-0._ret tbms.var 0
execute if score default.int.tuple_unpacking_ints.getResult-0.b tbms.var matches 10 if score default.int.tuple_unpacking_ints.getResult-0.a tbms.var matches 5 run scoreboard players set default.int.tuple_unpacking_ints.getResult-0._ret tbms.var 1
execute unless score default.int.tuple_unpacking_ints.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/22
execute if score default.int.tuple_unpacking_ints.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/23
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.tuple_unpacking_ints.enabled tbms.var 0
scoreboard players set default.int.tuple_unpacking_ints.callback tbms.var 0
