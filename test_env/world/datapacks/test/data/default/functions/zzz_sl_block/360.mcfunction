# ==================================================
# void default.zzz_sl_block.360()
# a.k.a default.test.TestRunner.next._19.1
# ==================================================

scoreboard players set default.test.TestRunner.running tbms.var 1
execute unless score default.int.neq_float_delta.enabled tbms.var = default.int.neq_float_delta.enabled tbms.var run scoreboard players set default.int.neq_float_delta.enabled tbms.var 0
execute if score default.int.neq_float_delta.enabled tbms.var matches 0 run function default:zzz_sl_block/211
