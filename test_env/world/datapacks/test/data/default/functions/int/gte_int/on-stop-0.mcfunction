# ==================================================
# void default.int.gte_int.onStop-0()
# a.k.a default.int.gte_int.onStop
# ==================================================

scoreboard players set default.int.gte_int.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/262
execute if score default.int.gte_int.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/263
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next