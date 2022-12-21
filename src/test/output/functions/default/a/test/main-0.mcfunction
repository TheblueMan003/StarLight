scoreboard players set default.a.test.main-0._2 tbms.var 0
execute if entity @s[x=0,dx=10,y=0,dy=256,z=0,dz=10] run function default/zzz_sl_block/2
execute if score default.a.test.main-0._2 tbms.var matches 0 unless score @s s-2042271694 matches 0 run function default/zzz_sl_block/4
