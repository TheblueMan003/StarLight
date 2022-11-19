scoreboard players set @s default.test.score 0
execute unless score default.test.test.test2 tbms.var = default.test.test.test2 tbms.var run scoreboard players set default.test.test.test2 tbms.var 0
execute unless score default.test.test.test3 tbms.var = default.test.test.test3 tbms.var run scoreboard players set default.test.test.test3 tbms.var 0
scoreboard players set default.test.test.test2 tbms.var 1673342187
scoreboard players set default.test.test.test3 tbms.var -316289040
scoreboard players operation default.zzz_sl_mux.int___to___int.fct tbms.var = default.test.test.test2 tbms.var
scoreboard players set default.zzz_sl_mux.int___to___int.a_0 tbms.var 5
function default/zzz_sl_mux/int___to___int
scoreboard players operation default.test.test.a tbms.var = default.zzz_sl_mux.int___to___int._ret tbms.var
execute if score default.test.test.a tbms.var matches 1.. run function default/zzz_sl_block/_0
function default/zzz_sl_block/_1
scoreboard players set default.test.test.a tbms.var 0
execute if score default.test.test.a tbms.var matches ..9 run function default/zzz_sl_block/_2
