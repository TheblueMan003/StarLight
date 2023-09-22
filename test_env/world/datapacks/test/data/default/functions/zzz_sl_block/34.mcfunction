# ==================================================
# void default.zzz_sl_block.34()
# a.k.a default.cmd.schedule.24.__lambda__
# ==================================================

schedule function default:int/ternary_operator_nested/crash 1 append
execute unless score default.int.ternary_operator_nested.enabled tbms.var matches 0 run function default:int/ternary_operator_nested/main
schedule clear default:int/ternary_operator_nested/crash
scoreboard players set default.int.ternary_operator_nested.crashCount tbms.var 0
execute unless score default.int.ternary_operator_nested.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/34 1 append
