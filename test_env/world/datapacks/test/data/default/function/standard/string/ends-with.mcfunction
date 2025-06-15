# ==================================================
# bool default.standard.string.endsWith(string source, string value)
# ==================================================
# ==================================================
#     Check if a string ends with another string.    
# ==================================================

data modify storage default.standard.string.0.inner._0 json.start set from storage default.standard.string.ends-with.source json
function default:zzz_sl_block/377 with storage default.standard.string.0.inner._0 json
data modify storage default.standard.string.equals.value json set string storage default.standard.string.ends-with.value json
function default:standard/string/equals
scoreboard players operation default.standard.string.endsWith._ret tbms.var = default.standard.string.equals._ret tbms.var
