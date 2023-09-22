# ==================================================
# bool default.int.ternary_operator_mixed_result.getResult-0()
# a.k.a default.int.ternary_operator_mixed_result.getResult
# ==================================================

scoreboard players set default.int.ternary_operator_mixed_result.getResult-0.a tbms.var 7
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0.result tbms.var = default.int.ternary_operator_mixed_result.getResult-0.a tbms.var
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0.result tbms.var *= c1000 tbms.const
scoreboard players set default.int.ternary_operator_mixed_result.getResult-0._ret tbms.var 0
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0._2 tbms.var = default.int.ternary_operator_mixed_result.getResult-0.a tbms.var
scoreboard players operation default.int.ternary_operator_mixed_result.getResult-0._2 tbms.var *= c1000 tbms.const
execute if score default.int.ternary_operator_mixed_result.getResult-0.result tbms.var = default.int.ternary_operator_mixed_result.getResult-0._2 tbms.var run scoreboard players set default.int.ternary_operator_mixed_result.getResult-0._ret tbms.var 1
