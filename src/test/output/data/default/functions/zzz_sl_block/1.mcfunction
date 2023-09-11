scoreboard players operation default.test.array.set.index tbms.var = default.test.1._0.i tbms.var
scoreboard players operation default.test.array.set.value tbms.var = default.test.1._0.i tbms.var
function default:test/array/set
scoreboard players add default.test.1._0.i tbms.var 1
execute if score default.test.1._0.i tbms.var matches ..51 run function default:zzz_sl_block/1
