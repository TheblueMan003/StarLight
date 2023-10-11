# ==================================================
# void default.zzz_sl_block.0()
# a.k.a default.test.test.a.start
# ==================================================
# ==================================================
#     Start the process    
# ==================================================

execute unless score default.test.test.a.enabled tbms.var = default.test.test.a.enabled tbms.var run scoreboard players set default.test.test.a.enabled tbms.var 0
execute if score default.test.test.a.enabled tbms.var matches 0 run function default:zzz_sl_block/11
