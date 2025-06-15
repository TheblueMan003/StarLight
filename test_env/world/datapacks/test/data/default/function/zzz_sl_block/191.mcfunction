# ==================================================
# void default.zzz_sl_block.191()
# a.k.a default.array.initer.start.1
# ==================================================

scoreboard players set default.array.initer.enabled tbms.var 1
scoreboard players set default.array.initer.time tbms.var 0
execute unless score default.array.initer.enabled tbms.var matches 0 run function default:zzz_sl_block/189
