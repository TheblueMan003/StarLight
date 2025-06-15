# ==================================================
# string default.standard.string.charAt(string source, int index)
# ==================================================
# ==================================================
#     Return the character at `index` in `source`.    
# ==================================================

execute if score default.standard.string.charAt.index tbms.var matches ..-1 run function default:zzz_sl_block/370
data modify storage default.standard.string.slice.source json set string storage default.standard.string.char-at.source json
scoreboard players operation default.standard.string.slice.start tbms.var = default.standard.string.charAt.index tbms.var
scoreboard players operation default.standard.string.slice.end tbms.var = default.standard.string.charAt.index tbms.var
scoreboard players add default.standard.string.slice.end tbms.var 1
function default:standard/string/slice
data modify storage default.standard.string.char-at._ret json set string storage default.standard.string.slice._ret json
