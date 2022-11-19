scoreboard players operation default.test.test.b tbms.var = default.test.test.a tbms.var
scoreboard players add default.test.test.a tbms.var 1
execute if score default.test.test.a tbms.var matches ..9 run function default/zzz_sl_block/_2
