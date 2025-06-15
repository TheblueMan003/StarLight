# ==================================================
# bool default.array.initer.getResult-0()
# a.k.a default.array.initer.getResult
# ==================================================

scoreboard players set default.array.initer.getResult-0._0 tbms.var 0
function default:zzz_sl_block/184
execute if score default.array.initer.getResult-0._0 tbms.var matches 0 run scoreboard players set default.array.initer.getResult-0._ret tbms.var 0
