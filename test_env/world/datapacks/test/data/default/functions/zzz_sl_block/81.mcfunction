# ==================================================
# void default.zzz_sl_block.81()
# a.k.a default.int.not_neq_float.stop.1
# ==================================================

scoreboard players set default.int.not_neq_float.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/79
execute if score default.int.not_neq_float.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/80
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.not_neq_float.enabled tbms.var 0
scoreboard players set default.int.not_neq_float.callback tbms.var 0
