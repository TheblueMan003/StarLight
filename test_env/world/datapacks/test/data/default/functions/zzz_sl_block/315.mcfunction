# ==================================================
# void default.zzz_sl_block.315()
# a.k.a default.int.sub_value.start.1
# ==================================================

scoreboard players set default.int.sub_value.enabled tbms.var 1
scoreboard players set default.int.sub_value.time tbms.var 0
execute unless score default.int.sub_value.enabled tbms.var matches 0 run function default:zzz_sl_block/313
