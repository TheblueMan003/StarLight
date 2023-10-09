# ==================================================
# void default.int.ternary_operator_mixed_result.onStop-0()
# a.k.a default.int.ternary_operator_mixed_result.onStop
# ==================================================

scoreboard players set default.int.ternary_operator_mixed_result.onStop-0._0._1 tbms.var 0
scoreboard players set default.int.ternary_operator_mixed_result.getResult-0.a tbms.var 7
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0.result tbms.var = default.int.ternary_operator_mixed_result.getResult-0.a tbms.var
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0.result tbms.var *= c1000 tbms.const
scoreboard players set default.int.ternary_operator_mixed_result.getResult-0._ret tbms.var 0
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0._2 tbms.var = default.int.ternary_operator_mixed_result.getResult-0.a tbms.var
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0._2 tbms.var *= c1000 tbms.const
execute if score default.int.ternary_operator_mixed_result.getResult-0.result tbms.var = default.int.ternary_operator_mixed_result.getResult-0._2 tbms.var run scoreboard players set default.int.ternary_operator_mixed_result.getResult-0._ret tbms.var 1
execute unless score default.int.ternary_operator_mixed_result.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/39
execute if score default.int.ternary_operator_mixed_result.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/40
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
