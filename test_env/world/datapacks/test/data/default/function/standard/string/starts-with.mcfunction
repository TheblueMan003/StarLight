# ==================================================
# bool default.standard.string.startsWith(string source, string value)
# ==================================================
# ==================================================
#     Check if a string starts with another string.    
# ==================================================

data modify storage default.standard.string.slice.source json set string storage default.standard.string.starts-with.source json
scoreboard players set default.standard.string.slice.start tbms.var 0
scoreboard players set default.standard.string.slice.end tbms.var 0
function default:standard/string/slice
data modify storage default.standard.string.equals.source json set string storage default.standard.string.slice._ret json
data modify storage default.standard.string.equals.value json set string storage default.standard.string.starts-with.value json
function default:standard/string/equals
scoreboard players operation default.standard.string.startsWith._ret tbms.var = default.standard.string.equals._ret tbms.var
