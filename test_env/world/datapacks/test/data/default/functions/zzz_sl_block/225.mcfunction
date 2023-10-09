# ==================================================
# void default.zzz_sl_block.225()
# a.k.a default.cmd.schedule.168.__lambda__
# ==================================================

schedule function default:int/lte_float/crash 1 append
execute unless score default.int.lte_float.enabled tbms.var matches 0 run function default:int/lte_float/main
schedule clear default:int/lte_float/crash
scoreboard players set default.int.lte_float.crashCount tbms.var 0
execute unless score default.int.lte_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/225 1 append
