# ==================================================
# void default.zzz_sl_block.185()
# a.k.a default.int.not_addition_variable.stop.1
# ==================================================

scoreboard players set default.int.not_addition_variable.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.not_addition_variable.getResult-0.a tbms.var 1
scoreboard players set default.int.not_addition_variable.getResult-0.b tbms.var 2
scoreboard players operation default.int.not_addition_variable.getResult-0.c tbms.var = default.int.not_addition_variable.getResult-0.a tbms.var
scoreboard players operation default.int.not_addition_variable.getResult-0.c tbms.var += default.int.not_addition_variable.getResult-0.b tbms.var
scoreboard players set default.int.not_addition_variable.getResult-0._0 tbms.var 0
execute unless score default.int.not_addition_variable.getResult-0.c tbms.var matches 3 run function default:zzz_sl_block/182
execute if score default.int.not_addition_variable.getResult-0._0 tbms.var matches 0 run scoreboard players set default.int.not_addition_variable.getResult-0._ret tbms.var 1
execute unless score default.int.not_addition_variable.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/183
execute if score default.int.not_addition_variable.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/184
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.not_addition_variable.enabled tbms.var 0
scoreboard players set default.int.not_addition_variable.callback tbms.var 0
