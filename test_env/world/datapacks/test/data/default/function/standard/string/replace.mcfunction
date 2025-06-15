# ==================================================
# string default.standard.string.replace(string source, string value, string replacement)
# ==================================================
# ==================================================
#     Replace `value` with `replacement` in `source`.    
# ==================================================

data modify storage default.standard.string.replace.ret json set value ""
scoreboard players set default.standard.string.replace.c tbms.var 0
execute unless data storage default.standard.string.replace.source {json:""} run function default:zzz_sl_block/375
data modify storage default.standard.string.replace._ret json set string storage default.standard.string.replace.ret json
