execute unless score default.test.test.array tbms.var = default.test.test.array tbms.var run function default:zzz_sl_block/1
execute unless score default.test.test.b tbms.var = default.test.test.b tbms.var run scoreboard players set default.test.test.b tbms.var 0
scoreboard players set default.test.test.array.0 tbms.var 1
scoreboard players operation default.test.test.array.set.index tbms.var = default.test.test.b tbms.var
scoreboard players set default.test.test.array.set.value tbms.var 0
function default:zzz_sl_block/2
function default:test/pass
scoreboard players operation default.test.test.array.set.index tbms.var = default.test.test.b tbms.var
scoreboard players set default.test.test.array.set.value tbms.var 1
function default:zzz_sl_block/2
function default:test/pass
scoreboard players operation default.test.test.array.set.index tbms.var = default.test.test.b tbms.var
scoreboard players set default.test.test.array.set.value tbms.var 2
function default:zzz_sl_block/2
function default:test/pass
scoreboard players operation default.test.test.array.set.index tbms.var = default.test.test.b tbms.var
scoreboard players set default.test.test.array.set.value tbms.var 3
function default:zzz_sl_block/2
function default:test/pass
