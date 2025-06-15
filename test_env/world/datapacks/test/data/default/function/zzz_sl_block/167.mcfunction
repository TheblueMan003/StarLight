# ==================================================
# void default.zzz_sl_block.167()
# a.k.a default.array.addition.start.1
# ==================================================

scoreboard players set default.array.addition.enabled tbms.var 1
scoreboard players set default.array.addition.time tbms.var 0
execute unless score default.array.addition.enabled tbms.var matches 0 run function default:zzz_sl_block/165
