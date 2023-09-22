# ==================================================
# void default.zzz_sl_block.363()
# a.k.a default.test.TestRunner.next._22.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.ternary_operator_integer_result.enabled tbms.var = default.int.ternary_operator_integer_result.enabled tbms.var run scoreboard players set default.int.ternary_operator_integer_result.enabled tbms.var 0
execute if score default.int.ternary_operator_integer_result.enabled tbms.var matches 0 run function default:zzz_sl_block/60
