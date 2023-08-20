scoreboard players set default.d.lol.__hasFunctionReturned__ tbms.var 0
execute if score default.d.lol.a tbms.var matches 1.. run function default:zzz_sl_block/4
execute if score default.d.lol.__hasFunctionReturned__ tbms.var matches 0 if score default.d.lol.a tbms.var matches ..0 run function default:zzz_sl_block/8
