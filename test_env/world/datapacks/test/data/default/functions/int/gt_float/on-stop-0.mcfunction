# ==================================================
# void default.int.gt_float.onStop-0()
# a.k.a default.int.gt_float.onStop
# ==================================================

scoreboard players set default.int.gt_float.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/246
execute if score default.int.gt_float.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/247
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
