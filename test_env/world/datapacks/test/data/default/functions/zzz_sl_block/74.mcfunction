# ==================================================
# void default.zzz_sl_block.74()
# a.k.a default.cmd.schedule.54.__lambda__
# ==================================================

schedule function default:int/not_lte_float/crash 1 append
execute unless score default.int.not_lte_float.enabled tbms.var matches 0 run function default:int/not_lte_float/main
schedule clear default:int/not_lte_float/crash
scoreboard players set default.int.not_lte_float.crashCount tbms.var 0
execute unless score default.int.not_lte_float.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/74 1 append
