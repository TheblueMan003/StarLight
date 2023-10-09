# ==================================================
# bool default.int.ternary_operator_float_result.getResult-0()
# a.k.a default.int.ternary_operator_float_result.getResult
# ==================================================

scoreboard players set default.int.ternary_operator_float_result.getResult-0.x tbms.var 3500
scoreboard players operation default.int.ternary_operator_float_result.getResult-0.result tbms.var = default.int.ternary_operator_float_result.getResult-0.x tbms.var
scoreboard players set default.int.ternary_operator_float_result.getResult-0._ret tbms.var 0
execute if score default.int.ternary_operator_float_result.getResult-0.result tbms.var = default.int.ternary_operator_float_result.getResult-0.x tbms.var run scoreboard players set default.int.ternary_operator_float_result.getResult-0._ret tbms.var 1
