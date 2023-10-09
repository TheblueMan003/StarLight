# ==================================================
# bool default.int.multi_variable.getResult-0()
# a.k.a default.int.multi_variable.getResult
# ==================================================

scoreboard players set default.int.multi_variable.getResult-0.a tbms.var 1
scoreboard players set default.int.multi_variable.getResult-0.b tbms.var 2
scoreboard players operation default.int.multi_variable.getResult-0.c tbms.var = default.int.multi_variable.getResult-0.a tbms.var
scoreboard players operation default.int.multi_variable.getResult-0.c tbms.var *= default.int.multi_variable.getResult-0.b tbms.var
scoreboard players set default.int.multi_variable.getResult-0._0 tbms.var 0
execute if score default.int.multi_variable.getResult-0.c tbms.var matches 2 run function default:zzz_sl_block/325
execute if score default.int.multi_variable.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.multi_variable.getResult-0._ret tbms.var 0
