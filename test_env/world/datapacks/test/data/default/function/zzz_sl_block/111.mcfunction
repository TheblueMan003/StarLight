# ==================================================
# void default.zzz_sl_block.111()
# a.k.a default.array.sugar.start.1
# ==================================================

scoreboard players set default.array.sugar.enabled tbms.var 1
scoreboard players set default.array.sugar.time tbms.var 0
execute unless score default.array.sugar.enabled tbms.var matches 0 run function default:zzz_sl_block/109
