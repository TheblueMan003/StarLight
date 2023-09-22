# ==================================================
# bool default.int.ternary_operator_nested.getResult-0()
# a.k.a default.int.ternary_operator_nested.getResult
# ==================================================

scoreboard players set default.int.ternary_operator_nested.getResult-0.a tbms.var 10
scoreboard players set default.int.ternary_operator_nested.getResult-0.c tbms.var 30
scoreboard players operation default.int.ternary_operator_nested.getResult-0.result tbms.var = default.int.ternary_operator_nested.getResult-0.c tbms.var
scoreboard players set default.int.ternary_operator_nested.getResult-0._ret tbms.var 0
execute if score default.int.ternary_operator_nested.getResult-0.result tbms.var = default.int.ternary_operator_nested.getResult-0.a tbms.var run scoreboard players set default.int.ternary_operator_nested.getResult-0._ret tbms.var 1
