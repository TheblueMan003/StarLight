# ==================================================
# void default.zzz_sl_block.378()
# a.k.a default.test.TestRunner.next._37.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.ternary_operator_mixed_result.enabled tbms.var = default.int.ternary_operator_mixed_result.enabled tbms.var run scoreboard players set default.int.ternary_operator_mixed_result.enabled tbms.var 0
execute if score default.int.ternary_operator_mixed_result.enabled tbms.var matches 0 run function default:zzz_sl_block/44
