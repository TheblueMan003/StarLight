# ==================================================
# void default.zzz_sl_block.240()
# a.k.a default.int.eq_float.stop.1
# ==================================================

scoreboard players set default.int.eq_float.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/238
execute if score default.int.eq_float.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/239
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.eq_float.enabled tbms.var 0
scoreboard players set default.int.eq_float.callback tbms.var 0
