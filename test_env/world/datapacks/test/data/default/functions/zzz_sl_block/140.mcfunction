# ==================================================
# void default.zzz_sl_block.140()
# a.k.a default.int.not_eq_int.start.1
# ==================================================

scoreboard players set default.int.not_eq_int.enabled tbms.var 1
scoreboard players set default.int.not_eq_int.time tbms.var 0
execute unless score default.int.not_eq_int.enabled tbms.var matches 0 run function default:zzz_sl_block/138