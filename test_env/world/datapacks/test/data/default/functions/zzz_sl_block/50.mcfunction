# ==================================================
# void default.zzz_sl_block.50()
# a.k.a default.cmd.schedule.36.__lambda__
# ==================================================

schedule function default:int/ternary_operator_float_result/crash 1 append
execute unless score default.int.ternary_operator_float_result.enabled tbms.var matches 0 run function default:int/ternary_operator_float_result/main
schedule clear default:int/ternary_operator_float_result/crash
scoreboard players set default.int.ternary_operator_float_result.crashCount tbms.var 0
execute unless score default.int.ternary_operator_float_result.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/50 1 append
