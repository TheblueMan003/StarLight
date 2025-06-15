# ==================================================
# void default.zzz_sl_block.374()
# a.k.a default.standard.string.replace._0.5
# ==================================================

data modify storage default.standard.string.replace._0._4._0 json set string storage default.standard.string.replace.source json 0 1
data modify storage default.standard.string.concat._0 json.a set from storage default.standard.string.replace.ret json
data modify storage default.standard.string.concat._0 json.b set from storage default.standard.string.replace._0._4._0 json
function default:standard/string/concat with storage default.standard.string.concat._0 json
data modify storage default.standard.string.replace.ret json set string storage default.standard.string.concat._ret json
data modify storage default.standard.string.replace.source json set string storage default.standard.string.replace.source json 1
