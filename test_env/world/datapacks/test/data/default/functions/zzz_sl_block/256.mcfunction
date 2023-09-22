# ==================================================
# void default.zzz_sl_block.256()
# a.k.a default.int.lt_float.stop.1
# ==================================================

scoreboard players set default.int.lt_float.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/254
execute if score default.int.lt_float.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/255
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.lt_float.enabled tbms.var 0
scoreboard players set default.int.lt_float.callback tbms.var 0
