scoreboard players set default.zzz_sl_mux.int___to___int.fct tbms.var 0
scoreboard players set default.test.abs._0 tbms.var 0
execute if score default.test.abs.x tbms.var matches 1.. run function default/zzz_sl_block/_4
scoreboard players set default.test.abs._ret tbms.var 0
scoreboard players operation default.test.abs._ret tbms.var -= default.test.abs.x tbms.var
