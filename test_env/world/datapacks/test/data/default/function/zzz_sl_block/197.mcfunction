# ==================================================
# void default.zzz_sl_block.197()
# a.k.a default.cmd.schedule.18.__lambda__
# ==================================================

scoreboard players set default.test.TestRunner.crashCount tbms.var 0
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/197 1 append
