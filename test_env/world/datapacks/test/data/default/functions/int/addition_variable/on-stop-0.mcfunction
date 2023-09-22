# ==================================================
# void default.int.addition_variable.onStop-0()
# a.k.a default.int.addition_variable.onStop
# ==================================================

scoreboard players set default.int.addition_variable.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.addition_variable.getResult-0.a tbms.var 1
scoreboard players set default.int.addition_variable.getResult-0.b tbms.var 2
scoreboard players operation default.int.addition_variable.getResult-0.c tbms.var = default.int.addition_variable.getResult-0.a tbms.var
scoreboard players operation default.int.addition_variable.getResult-0.c tbms.var += default.int.addition_variable.getResult-0.b tbms.var
scoreboard players set default.int.addition_variable.getResult-0._0 tbms.var 0
execute if score default.int.addition_variable.getResult-0.c tbms.var matches 3 run function default:zzz_sl_block/333
execute if score default.int.addition_variable.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.addition_variable.getResult-0._ret tbms.var 0
execute unless score default.int.addition_variable.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/334
execute if score default.int.addition_variable.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/335
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
