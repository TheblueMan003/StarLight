# ==================================================
# void default.array.initer.onStop-0()
# a.k.a default.array.initer.onStop
# ==================================================

scoreboard players set default.array.initer.onStop-0._0._1 tbms.var 0
scoreboard players set default.array.initer.getResult-0._0 tbms.var 0
function default:zzz_sl_block/184
execute if score default.array.initer.getResult-0._0 tbms.var matches 0 run scoreboard players set default.array.initer.getResult-0._ret tbms.var 0
execute unless score default.array.initer.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/186
execute if score default.array.initer.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/187
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
