scoreboard players set default.standard.int.pow._0 tbms.var 0
execute if score default.standard.int.pow.n tbms.var matches 0 run function default:zzz_sl_block/1
execute if score default.standard.int.pow.n tbms.var matches 1 if score default.standard.int.pow._0 tbms.var matches 0 run function default:zzz_sl_block/9
execute if score default.standard.int.pow._0 tbms.var matches 0 run function default:zzz_sl_block/11
