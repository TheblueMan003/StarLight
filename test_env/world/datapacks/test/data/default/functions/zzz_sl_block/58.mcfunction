# ==================================================
# void default.zzz_sl_block.58()
# a.k.a default.cmd.schedule.42.__lambda__
# ==================================================

schedule function default:int/ternary_operator_integer_result/crash 1 append
execute unless score default.int.ternary_operator_integer_result.enabled tbms.var matches 0 run function default:int/ternary_operator_integer_result/main
schedule clear default:int/ternary_operator_integer_result/crash
scoreboard players set default.int.ternary_operator_integer_result.crashCount tbms.var 0
execute unless score default.int.ternary_operator_integer_result.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/58 1 append
