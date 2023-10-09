# ==================================================
# void default.zzz_sl_block.321()
# a.k.a default.cmd.schedule.240.__lambda__
# ==================================================

schedule function default:int/addition_value/crash 1 append
execute unless score default.int.addition_value.enabled tbms.var matches 0 run function default:int/addition_value/main
schedule clear default:int/addition_value/crash
scoreboard players set default.int.addition_value.crashCount tbms.var 0
execute unless score default.int.addition_value.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/321 1 append
