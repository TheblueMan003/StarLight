# ==================================================
# default.standard.string.StringIterator default.standard.string.iterator(json a)
# ==================================================
# ==================================================
#     Return a string iterator    
# ==================================================

data modify storage default.standard.string.cast._0 json.a set from storage default.standard.string.iterator.a json
function default:standard/string/cast with storage default.standard.string.cast._0 json
data modify storage default.standard.string.iterator._ret.__init__.data json set string storage default.standard.string.cast._ret json
data modify storage default.standard.string.iterator._ret.data json set string storage default.standard.string.iterator._ret.__init__.data json
