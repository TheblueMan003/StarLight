# ==================================================
# void default.zzz_sl_block.273()
# a.k.a default.cmd.schedule.204.__lambda__
# ==================================================

schedule function default:int/lte_int/crash 1 append
execute unless score default.int.lte_int.enabled tbms.var matches 0 run function default:int/lte_int/main
schedule clear default:int/lte_int/crash
scoreboard players set default.int.lte_int.crashCount tbms.var 0
execute unless score default.int.lte_int.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/273 1 append
