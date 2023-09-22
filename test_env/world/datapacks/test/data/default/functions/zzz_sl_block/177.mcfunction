# ==================================================
# void default.zzz_sl_block.177()
# a.k.a default.int.not_multi_variable.stop.1
# ==================================================

scoreboard players set default.int.not_multi_variable.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.not_multi_variable.getResult-0.a tbms.var 1
scoreboard players set default.int.not_multi_variable.getResult-0.b tbms.var 2
scoreboard players operation default.int.not_multi_variable.getResult-0.c tbms.var = default.int.not_multi_variable.getResult-0.a tbms.var
scoreboard players operation default.int.not_multi_variable.getResult-0.c tbms.var *= default.int.not_multi_variable.getResult-0.b tbms.var
scoreboard players set default.int.not_multi_variable.getResult-0._0 tbms.var 0
execute unless score default.int.not_multi_variable.getResult-0.c tbms.var matches 2 run function default:zzz_sl_block/174
execute if score default.int.not_multi_variable.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.not_multi_variable.getResult-0._ret tbms.var 1
execute unless score default.int.not_multi_variable.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/175
execute if score default.int.not_multi_variable.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/176
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.not_multi_variable.enabled tbms.var 0
scoreboard players set default.int.not_multi_variable.callback tbms.var 0
