# ==================================================
# void default.int.eq_int.onStop-0()
# a.k.a default.int.eq_int.onStop
# ==================================================

scoreboard players set default.int.eq_int.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/286
execute if score default.int.eq_int.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/287
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
