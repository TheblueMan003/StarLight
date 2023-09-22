# ==================================================
# void default.zzz_sl_block.42()
# a.k.a default.cmd.schedule.30.__lambda__
# ==================================================

schedule function default:int/ternary_operator_mixed_result/crash 1 append
execute unless score default.int.ternary_operator_mixed_result.enabled tbms.var matches 0 run function default:int/ternary_operator_mixed_result/main
schedule clear default:int/ternary_operator_mixed_result/crash
scoreboard players set default.int.ternary_operator_mixed_result.crashCount tbms.var 0
execute unless score default.int.ternary_operator_mixed_result.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/42 1 append
