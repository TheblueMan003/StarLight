# ==================================================
# void default.int.ternary_operator_integer_result.onStop-0()
# a.k.a default.int.ternary_operator_integer_result.onStop
# ==================================================

scoreboard players set default.int.ternary_operator_integer_result.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.ternary_operator_integer_result.getResult-0.a tbms.var 5
scoreboard players operation default.int.ternary_operator_integer_result.getResult-0.result tbms.var = default.int.ternary_operator_integer_result.getResult-0.a tbms.var
scoreboard players set default.int.ternary_operator_integer_result.getResult-0._ret tbms.var 0
execute if score default.int.ternary_operator_integer_result.getResult-0.result tbms.var = default.int.ternary_operator_integer_result.getResult-0.a tbms.var run scoreboard players set default.int.ternary_operator_integer_result.getResult-0._ret tbms.var 1
execute unless score default.int.ternary_operator_integer_result.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/55
execute if score default.int.ternary_operator_integer_result.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/56
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
