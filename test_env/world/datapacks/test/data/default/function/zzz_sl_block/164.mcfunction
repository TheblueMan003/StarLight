# ==================================================
# void default.zzz_sl_block.164()
# a.k.a default.array.addition.stop.1
# ==================================================

scoreboard players set default.array.addition.onStop-0._0._1 tbms.var 0
scoreboard players set default.array.addition.getResult-0.vec1.x tbms.var 10000
scoreboard players set default.array.addition.getResult-0.vec1.y tbms.var 20000
scoreboard players set default.array.addition.getResult-0.vec2.x tbms.var 10000
scoreboard players set default.array.addition.getResult-0.vec2.y tbms.var 20000
scoreboard players operation default.array.addition.getResult-0.vec3.x tbms.var = default.array.addition.getResult-0.vec1.x tbms.var
scoreboard players operation default.array.addition.getResult-0.vec3.y tbms.var = default.array.addition.getResult-0.vec1.y tbms.var
scoreboard players operation default.array.addition.getResult-0.vec3.x tbms.var += default.array.addition.getResult-0.vec2.x tbms.var
scoreboard players operation default.array.addition.getResult-0.vec3.y tbms.var += default.array.addition.getResult-0.vec2.y tbms.var
scoreboard players set default.array.addition.getResult-0._0 tbms.var 0
execute if score default.array.addition.getResult-0.vec3.y tbms.var matches 40000 if score default.array.addition.getResult-0.vec3.x tbms.var matches 20000 run function default:zzz_sl_block/158
execute if score default.array.addition.getResult-0._0 tbms.var matches 0 run scoreboard players set default.array.addition.getResult-0._ret tbms.var 0
execute unless score default.array.addition.getResult-0._ret tbms.var matches 0 run function default:zzz_sl_block/162
execute if score default.array.addition.onStop-0._0._1 tbms.var matches 0 run function default:zzz_sl_block/163
scoreboard players add default.test.__total__ tbms.var 1
function default:test/-test-runner/next
scoreboard players set default.array.addition.enabled tbms.var 0
scoreboard players operation default.zzz_sl_mux.void___to___void.__fct__ tbms.var = default.array.addition.callback tbms.var
function default:zzz_sl_block/107
scoreboard players set default.array.addition.callback tbms.var 0
