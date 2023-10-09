# ==================================================
# void default.zzz_sl_block.272()
# a.k.a default.int.lte_int.stop.1
# ==================================================

scoreboard players set default.int.lte_int.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/270
execute if score default.int.lte_int.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/271
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.lte_int.enabled tbms.var 0
scoreboard players set default.int.lte_int.callback tbms.var 0
