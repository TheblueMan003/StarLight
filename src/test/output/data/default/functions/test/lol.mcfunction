# ==================================================
# void default.test.lol()
# ==================================================

data modify storage default.__string_concat__._0 json set value " world"
function default:__string_concat__ with storage default.__string_concat__._0 json
tellraw @a [{"nbt": "json", "storage":"tbms:var","bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
