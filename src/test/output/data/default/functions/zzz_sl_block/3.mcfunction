# ==================================================
# void default.zzz_sl_block.3()
# a.k.a default.cmd.schedule.0.__lambda__
# ==================================================

function default:zzz_sl_block/4
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/3 1 append
