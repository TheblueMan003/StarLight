# ==================================================
# void default.int.gt_int.onStop-0()
# a.k.a default.int.gt_int.onStop
# ==================================================

scoreboard players set default.int.gt_int.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/294
execute if score default.int.gt_int.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/295
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
