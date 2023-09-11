summon marker ~ ~ ~ {Tags:["random.trg"]}
execute as @e[tag=random.trg,limit=1] run function default:zzz_sl_block/4
scoreboard players operation default.test.1.swap.a tbms.var %= c52 tbms.const
scoreboard players add default.test.1.swap.a tbms.var 0
summon marker ~ ~ ~ {Tags:["random.trg"]}
execute as @e[tag=random.trg,limit=1] run function default:zzz_sl_block/5
scoreboard players operation default.test.1.swap.b tbms.var %= c52 tbms.const
scoreboard players add default.test.1.swap.b tbms.var 0
scoreboard players operation default.test.array.get.index tbms.var = default.test.1.swap.a tbms.var
function default:test/array/get
scoreboard players operation default.test.1.swap.t tbms.var = default.test.array.get._ret tbms.var
scoreboard players operation default.test.array.set.index tbms.var = default.test.1.swap.a tbms.var
scoreboard players operation default.test.array.get.index tbms.var = default.test.1.swap.b tbms.var
function default:test/array/get
scoreboard players operation default.test.array.set.value tbms.var = default.test.array.get._ret tbms.var
function default:test/array/set
scoreboard players operation default.test.array.set.index tbms.var = default.test.1.swap.b tbms.var
scoreboard players operation default.test.array.set.value tbms.var = default.test.1.swap.t tbms.var
function default:test/array/set
scoreboard players add default.test.1._1.i tbms.var 1
execute if score default.test.1._1.i tbms.var matches ..199 run function default:zzz_sl_block/3
