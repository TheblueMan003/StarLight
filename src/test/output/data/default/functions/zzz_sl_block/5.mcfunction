# ==================================================
# void default.zzz_sl_block.5()
# a.k.a default.cmd.schedule.0.__lambda__
# ==================================================

function default:zzz_sl_block/8
execute unless score default.test.test.a.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/5 1 append
