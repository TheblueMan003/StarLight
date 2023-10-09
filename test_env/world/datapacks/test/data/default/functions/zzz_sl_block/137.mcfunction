# ==================================================
# void default.zzz_sl_block.137()
# a.k.a default.int.not_eq_int.stop.1
# ==================================================

scoreboard players set default.int.not_eq_int.onStop-0._0._1 tbms.var 0
function default:zzz_sl_block/135
execute if score default.int.not_eq_int.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/136
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.int.not_eq_int.enabled tbms.var 0
scoreboard players set default.int.not_eq_int.callback tbms.var 0
