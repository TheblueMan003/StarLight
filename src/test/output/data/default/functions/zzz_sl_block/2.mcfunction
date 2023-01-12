function default:zzz_sl_block/3
execute unless score default.a.adas.enabled tbms.var matches 0 run schedule function default:zzz_sl_block/2 1t append
