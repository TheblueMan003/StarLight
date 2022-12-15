scoreboard players remove default.time.t tbms.var 1
execute if score default.time.t tbms.var matches ..-1 run function default/zzz_sl_block/0
execute if score default.time.s tbms.var matches ..-1 run function default/zzz_sl_block/1
execute if score default.time.m tbms.var matches ..-1 run function default/zzz_sl_block/2
execute if score default.time.t tbms.var matches ..0 if score default.time.s tbms.var matches ..0 if score default.time.m tbms.var matches ..0 if score default.time.h tbms.var matches ..0 run function default/zzz_sl_block/4
