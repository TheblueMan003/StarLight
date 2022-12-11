scoreboard players operation default.math.isClose.diff tbms.var = default.math.isClose.x tbms.var
scoreboard players operation default.math.isClose.diff tbms.var -= default.math.isClose.y tbms.var
scoreboard players operation default.math.abs-0.x tbms.var = default.math.isClose.diff tbms.var
function default:math/abs-0
scoreboard players operation default.math.isClose.diff tbms.var = default.math.abs-0._ret tbms.var
execute if score default.math.isClose.diff tbms.var < default.math.isClose.maxDiff tbms.var run scoreboard players set default.math.isClose._ret tbms.var 1
execute if score default.math.isClose.diff tbms.var >= default.math.isClose.maxDiff tbms.var run scoreboard players set default.math.isClose._ret tbms.var 0
