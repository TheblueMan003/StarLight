scoreboard players set default.zzz_sl_mux.float___to___float.fct tbms.var 0
scoreboard players set default.test.abs__overloading__0._0 tbms.var 0
execute if score default.test.abs__overloading__0.x tbms.var matches 1.. run function default/zzz_sl_block/_3
scoreboard players set default.test.abs__overloading__0._ret tbms.var 0
scoreboard players operation default.test.abs__overloading__0._ret tbms.var -= default.test.abs__overloading__0.x tbms.var
