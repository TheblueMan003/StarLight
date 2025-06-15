# ==================================================
# string default.standard.string.toLower(string source)
# ==================================================
# ==================================================
#     Make the string lowercase.    
# ==================================================

data modify storage default.standard.string.to-lower.ret json set value ""
scoreboard players set default.standard.string.toLower.c tbms.var 0
execute unless data storage default.standard.string.to-lower.source {json:""} run function default:zzz_sl_block/366
data modify storage default.standard.string.to-lower._ret json set string storage default.standard.string.to-lower.ret json
