# ==================================================
# void default.zzz_sl_block.322()
# a.k.a default.cmd.schedule.0.__lambda__
# ==================================================

scoreboard players set default.test.TestRunner.crashCount tbms.var 0
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/322 1 append
