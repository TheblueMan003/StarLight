# ==================================================
# void default.zzz_sl_block.1()
# a.k.a default.test.test.a.stop
# ==================================================
# ==================================================
#     Stop the process    
# ==================================================

execute unless score default.test.test.a.enabled tbms.var matches 0 run function default:zzz_sl_block/4
