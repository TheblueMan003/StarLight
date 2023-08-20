scoreboard players operation default.standard.int.pow._5.parity tbms.var = default.standard.int.pow.x tbms.var
scoreboard players operation default.standard.int.pow._5.parity tbms.var %= c2 tbms.const
execute if score default.standard.int.pow._5.parity tbms.var matches 1 run function default:zzz_sl_block/10
scoreboard players operation default.standard.int.pow.n tbms.var /= c2 tbms.const
scoreboard players operation default.standard.int.pow.x tbms.var = default.standard.int.pow.x tbms.var
scoreboard players operation default.standard.int.pow.n tbms.var = default.standard.int.pow.n tbms.var
scoreboard players operation default.standard.int.pow.m tbms.var = default.standard.int.pow.m tbms.var
function default:standard/int/pow
scoreboard players operation default.standard.int.pow._ret tbms.var = default.standard.int.pow._ret tbms.var
