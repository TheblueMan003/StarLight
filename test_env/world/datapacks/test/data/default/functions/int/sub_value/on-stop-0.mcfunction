# ==================================================
# void default.int.sub_value.onStop-0()
# a.k.a default.int.sub_value.onStop
# ==================================================

scoreboard players set default.int.sub_value.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.sub_value.getResult-0.a tbms.var 1
scoreboard players operation default.int.sub_value.getResult-0.c tbms.var = default.int.sub_value.getResult-0.a tbms.var
scoreboard players remove default.int.sub_value.getResult-0.c tbms.var 2
scoreboard players set default.int.sub_value.getResult-0._0 tbms.var 0
execute if score default.int.sub_value.getResult-0.c tbms.var matches -1 run function default:zzz_sl_block/309
execute if score default.int.sub_value.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.sub_value.getResult-0._ret tbms.var 0
execute unless score default.int.sub_value.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/310
execute if score default.int.sub_value.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/311
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
