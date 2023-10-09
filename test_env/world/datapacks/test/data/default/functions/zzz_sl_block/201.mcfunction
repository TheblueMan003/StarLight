# ==================================================
# void default.zzz_sl_block.201()
# a.k.a default.int.neq_float_fail.stop.1
# ==================================================

scoreboard players set default.int.neq_float_fail.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/199
execute if score default.int.neq_float_fail.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/200
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.neq_float_fail.enabled tbms.var 0
scoreboard players set default.int.neq_float_fail.callback tbms.var 0
