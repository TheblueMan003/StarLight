# ==================================================
# bool default.math.isClose(float x, float y, float maxDiff = 0.01)
# ==================================================
# ==================================================
# return true if difference between x and y smaller than maxDiff
# ==================================================

scoreboard players operation default.math.abs---444556297.impl.x tbms.var = default.math.isClose.x tbms.var
scoreboard players operation default.math.abs---444556297.impl.x tbms.var -= default.math.isClose.y tbms.var
function default:zzz_sl_block/102
scoreboard players operation default.math.isClose.diff tbms.var = default.math.abs---444556297.impl._ret tbms.var
scoreboard players set default.math.isClose._0 tbms.var 0
execute if score default.math.isClose.diff tbms.var < default.math.isClose.maxDiff tbms.var run function default:zzz_sl_block/103
execute if score default.math.isClose._0 tbms.var matches 0 run scoreboard players set default.math.isClose._ret tbms.var 0
