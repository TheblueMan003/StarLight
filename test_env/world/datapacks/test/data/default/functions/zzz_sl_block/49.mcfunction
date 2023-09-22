# ==================================================
# void default.zzz_sl_block.49()
# a.k.a default.int.ternary_operator_float_result.stop.1
# ==================================================

scoreboard players set default.int.ternary_operator_float_result.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.ternary_operator_float_result.getResult-0.x tbms.var 3500
scoreboard players operation default.int.ternary_operator_float_result.getResult-0.result tbms.var = default.int.ternary_operator_float_result.getResult-0.x tbms.var
scoreboard players set default.int.ternary_operator_float_result.getResult-0._ret tbms.var 0
execute if score default.int.ternary_operator_float_result.getResult-0.result tbms.var = default.int.ternary_operator_float_result.getResult-0.x tbms.var run scoreboard players set default.int.ternary_operator_float_result.getResult-0._ret tbms.var 1
execute unless score default.int.ternary_operator_float_result.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/47
execute if score default.int.ternary_operator_float_result.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/48
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.ternary_operator_float_result.enabled tbms.var 0
scoreboard players set default.int.ternary_operator_float_result.callback tbms.var 0
