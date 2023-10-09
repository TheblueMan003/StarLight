# ==================================================
# void default.zzz_sl_block.33()
# a.k.a default.int.ternary_operator_nested.stop.1
# ==================================================

scoreboard players set default.int.ternary_operator_nested.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.ternary_operator_nested.getResult-0.a tbms.var 10
scoreboard players set default.int.ternary_operator_nested.getResult-0.c tbms.var 30
scoreboard players operation default.int.ternary_operator_nested.getResult-0.result tbms.var = default.int.ternary_operator_nested.getResult-0.c tbms.var
scoreboard players set default.int.ternary_operator_nested.getResult-0._ret tbms.var 0
execute if score default.int.ternary_operator_nested.getResult-0.result tbms.var = default.int.ternary_operator_nested.getResult-0.a tbms.var run scoreboard players set default.int.ternary_operator_nested.getResult-0._ret tbms.var 1
execute unless score default.int.ternary_operator_nested.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/31
execute if score default.int.ternary_operator_nested.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/32
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.ternary_operator_nested.enabled tbms.var 0
scoreboard players set default.int.ternary_operator_nested.callback tbms.var 0
