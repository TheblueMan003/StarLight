# ==================================================
# void default.utils.process_manager.stopAll()
# ==================================================
# ==================================================
# Stop all the active processes
# ==================================================

execute unless score default.array.initer.enabled tbms.var matches 0 run function default:zzz_sl_block/188
execute unless score default.test.TestRunner.enabled tbms.var matches 0 run function default:zzz_sl_block/196
execute unless score default.array.addition.enabled tbms.var matches 0 run function default:zzz_sl_block/164
execute unless score default.array.sugar.enabled tbms.var matches 0 run function default:zzz_sl_block/108
