# ==================================================
# float default.zzz_sl_block.468(float guess)
# a.k.a default.math.sqrt._0.iterate
# ==================================================

scoreboard players operation default.math.sqrt._0.iterate.next tbms.var = default.math.sqrt.value tbms.var
scoreboard players operation default.math.sqrt._0.iterate.next tbms.var /= default.math.sqrt._0.iterate.guess tbms.var
scoreboard players operation default.math.sqrt._0.iterate.next tbms.var *= c1000 tbms.const
scoreboard players operation default.math.sqrt._0.iterate.next tbms.var += default.math.sqrt._0.iterate.guess tbms.var
scoreboard players operation default.math.sqrt._0.iterate.next tbms.var /= c2 tbms.const
scoreboard players operation default.math.isClose.x tbms.var = default.math.sqrt._0.iterate.next tbms.var
scoreboard players operation default.math.isClose.y tbms.var = default.math.sqrt._0.iterate.guess tbms.var
scoreboard players set default.math.isClose.maxDiff tbms.var 10
function default:math/is-close
scoreboard players set default.math.sqrt._0.iterate._1 tbms.var 0
scoreboard players operation default.math.isClose.x tbms.var = default.math.sqrt._0.iterate.next tbms.var
scoreboard players operation default.math.isClose.y tbms.var = default.math.sqrt._0.iterate.guess tbms.var
scoreboard players set default.math.isClose.maxDiff tbms.var 10
function default:math/is-close
execute unless score default.math.isClose._ret tbms.var matches 0 run function default:zzz_sl_block/470
execute if score default.math.sqrt._0.iterate._1 tbms.var matches 0 run function default:zzz_sl_block/471
