# ==================================================
# void default.test.test()
# ==================================================

execute unless score default.test.test.a tbms.var = default.test.test.a tbms.var run function default:zzz_sl_block/1
data modify storage default.test.t json.lol set value []
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.0 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.1 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.2 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.3 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.4 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.5 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.6 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.7 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.8 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result storage default.test.t tmp int 1.00000 run scoreboard players get default.test.test.a.lol.9 tbms.var
data modify storage default.test.t json.lol append from storage default.test.t tmp
execute store result score default.test.test.a.lol.0 tbms.var run data get storage default.test.t json.lol[0] 1.00000
execute store result score default.test.test.a.lol.1 tbms.var run data get storage default.test.t json.lol[1] 1.00000
execute store result score default.test.test.a.lol.2 tbms.var run data get storage default.test.t json.lol[2] 1.00000
execute store result score default.test.test.a.lol.3 tbms.var run data get storage default.test.t json.lol[3] 1.00000
execute store result score default.test.test.a.lol.4 tbms.var run data get storage default.test.t json.lol[4] 1.00000
execute store result score default.test.test.a.lol.5 tbms.var run data get storage default.test.t json.lol[5] 1.00000
execute store result score default.test.test.a.lol.6 tbms.var run data get storage default.test.t json.lol[6] 1.00000
execute store result score default.test.test.a.lol.7 tbms.var run data get storage default.test.t json.lol[7] 1.00000
execute store result score default.test.test.a.lol.8 tbms.var run data get storage default.test.t json.lol[8] 1.00000
execute store result score default.test.test.a.lol.9 tbms.var run data get storage default.test.t json.lol[9] 1.00000
