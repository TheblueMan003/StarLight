scoreboard players operation default.math.sqrt.iterate.next tbms.var = default.math.sqrt.value tbms.var
scoreboard players operation default.math.sqrt.iterate.next tbms.var /= default.math.sqrt.iterate.guess tbms.var
scoreboard players operation default.math.sqrt.iterate.next tbms.var *= 1000 tbms.const
scoreboard players operation default.math.sqrt.iterate.next tbms.var += default.math.sqrt.iterate.guess tbms.var
scoreboard players operation default.math.sqrt.iterate.next tbms.var /= 2 tbms.const
scoreboard players operation default.math.isClose.x tbms.var = default.math.sqrt.iterate.next tbms.var
scoreboard players operation default.math.isClose.y tbms.var = default.math.sqrt.iterate.guess tbms.var
scoreboard players set default.math.isClose.maxDiff tbms.var 10
function default:math/is-close
scoreboard players operation default.math.sqrt.iterate.close tbms.var = default.math.isClose._ret tbms.var
scoreboard players set default.math.sqrt.iterate._1 tbms.var 0
scoreboard players operation default.math.isClose.x tbms.var = default.math.sqrt.iterate.next tbms.var
scoreboard players operation default.math.isClose.y tbms.var = default.math.sqrt.iterate.guess tbms.var
scoreboard players set default.math.isClose.maxDiff tbms.var 10
function default:math/is-close
scoreboard players operation default.math.sqrt.iterate._0 tbms.var = default.math.isClose._ret tbms.var
execute unless score default.math.sqrt.iterate._0 tbms.var matches 0 run function default/zzz_sl_block/_13
execute if score default.math.sqrt.iterate._1 tbms.var matches 0 run function default/zzz_sl_block/_14
