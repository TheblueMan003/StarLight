# ==================================================
# bool default.standard.string.equalsIgnoreCase(string source, string value)
# ==================================================
# ==================================================
#     Check if two strings are equal. Case insensitive.    
# ==================================================

data modify storage default.standard.string.to-lower.source json set string storage default.standard.string.equals-ignore-case.source json
function default:standard/string/to-lower
data modify storage default.standard.string.equals.source json set string storage default.standard.string.to-lower._ret json
data modify storage default.standard.string.to-lower.source json set string storage default.standard.string.equals-ignore-case.value json
function default:standard/string/to-lower
data modify storage default.standard.string.equals.value json set string storage default.standard.string.to-lower._ret json
function default:standard/string/equals
