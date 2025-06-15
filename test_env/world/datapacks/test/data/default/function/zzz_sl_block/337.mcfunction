# ==================================================
# void default.zzz_sl_block.337()
# a.k.a default.standard.string.split._0.5
# ==================================================

data modify storage default.standard.string.split._0._4._0 json set string storage default.standard.string.split.source json 0 1
data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.split.current json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.split._0._4._0 json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.split.current json set string storage default.standard.string.concat._ret json
data modify storage default.standard.string.split.source json set string storage default.standard.string.split.source json 1
