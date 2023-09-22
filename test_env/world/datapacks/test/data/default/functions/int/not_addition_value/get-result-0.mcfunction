# ==================================================
# bool default.int.not_addition_value.getResult-0()
# a.k.a default.int.not_addition_value.getResult
# ==================================================

scoreboard players set default.int.not_addition_value.getResult-0.a tbms.var 1
scoreboard players operation default.int.not_addition_value.getResult-0.c tbms.var = default.int.not_addition_value.getResult-0.a tbms.var
scoreboard players add default.int.not_addition_value.getResult-0.c tbms.var 2
scoreboard players set default.int.not_addition_value.getResult-0._0 tbms.var 0
execute unless score default.int.not_addition_value.getResult-0.c tbms.var matches 3 run function default:zzz_sl_block/166
execute if score default.int.not_addition_value.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.not_addition_value.getResult-0._ret tbms.var 1
