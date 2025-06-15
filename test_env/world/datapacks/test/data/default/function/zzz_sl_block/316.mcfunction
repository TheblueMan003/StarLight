# ==================================================
# string default.zzz_sl_block.316(int key)
# a.k.a default.standard.string.split._ret.get
# ==================================================

$data modify storage default.standard.string.cast._0 json.a set from storage default.standard.string.split._ret.data json[$(key)]
function default:standard/string/cast with storage default.standard.string.cast._0 json
data modify storage default.standard.string.split._ret.get._ret json set string storage default.standard.string.cast._ret json
