# ==================================================
# bool default.int.sub_value.getResult-0()
# a.k.a default.int.sub_value.getResult
# ==================================================

scoreboard players set default.int.sub_value.getResult-0.a tbms.var 1
scoreboard players operation default.int.sub_value.getResult-0.c tbms.var = default.int.sub_value.getResult-0.a tbms.var
scoreboard players remove default.int.sub_value.getResult-0.c tbms.var 2
scoreboard players set default.int.sub_value.getResult-0._0 tbms.var 0
execute if score default.int.sub_value.getResult-0.c tbms.var matches -1 run function default:zzz_sl_block/309
execute if score default.int.sub_value.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.sub_value.getResult-0._ret tbms.var 0
