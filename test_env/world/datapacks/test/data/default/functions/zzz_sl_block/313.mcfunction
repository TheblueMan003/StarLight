# ==================================================
# void default.zzz_sl_block.313()
# a.k.a default.cmd.schedule.234.__lambda__
# ==================================================

schedule function default:int/sub_value/crash 1 append
execute unless score default.int.sub_value.enabled tbms.var matches 0 run function default:int/sub_value/main
schedule clear default:int/sub_value/crash
scoreboard players set default.int.sub_value.crashCount tbms.var 0
execute unless score default.int.sub_value.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/313 1 append
