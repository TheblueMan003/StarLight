# ==================================================
# void default.zzz_sl_block.92()
# a.k.a default.int.not_eq_float.start.1
# ==================================================

scoreboard players set default.int.not_eq_float.enabled tbms.var 1
scoreboard players set default.int.not_eq_float.time tbms.var 0
execute unless score default.int.not_eq_float.enabled tbms.var matches 0 run function default:zzz_sl_block/90
