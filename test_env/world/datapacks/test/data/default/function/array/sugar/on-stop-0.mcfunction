# ==================================================
# void default.array.sugar.onStop-0()
# a.k.a default.array.sugar.onStop
# ==================================================

scoreboard players set default.array.sugar.onStop-0._0._1 tbms.var 0
scoreboard players set default.array.sugar.getResult-0.vec1.x tbms.var 10000
scoreboard players set default.array.sugar.getResult-0.vec1.y tbms.var 20000
scoreboard players set default.array.sugar.getResult-0.vec2.x tbms.var 10000
scoreboard players set default.array.sugar.getResult-0.vec2.y tbms.var 20000
scoreboard players operation default.array.sugar.getResult-0.vec3.x tbms.var = default.array.sugar.getResult-0.vec1.x tbms.var
scoreboard players operation default.array.sugar.getResult-0.vec3.y tbms.var = default.array.sugar.getResult-0.vec1.y tbms.var
scoreboard players operation default.array.sugar.getResult-0.vec3.x tbms.var += default.array.sugar.getResult-0.vec2.x tbms.var
scoreboard players operation default.array.sugar.getResult-0.vec3.y tbms.var += default.array.sugar.getResult-0.vec2.y tbms.var
scoreboard players set default.array.sugar.getResult-0._0 tbms.var 0
execute if score default.array.sugar.getResult-0.vec3.y tbms.var matches 40000 if score default.array.sugar.getResult-0.vec3.x tbms.var matches 20000 run function default:zzz_sl_block/101
execute if score default.array.sugar.getResult-0._0 tbms.var matches 0 run scoreboard players set default.array.sugar.getResult-0._ret tbms.var 0
execute unless score default.array.sugar.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/105
execute if score default.array.sugar.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/106
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
