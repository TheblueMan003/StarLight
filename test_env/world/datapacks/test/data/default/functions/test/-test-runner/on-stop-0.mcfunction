# ==================================================
# void default.test.TestRunner.onStop-0()
# a.k.a default.test.TestRunner.onStop
# ==================================================

tellraw @a [{"text": "=============[Test Completed]=============", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"gold"}]
tellraw @a [{"text": ">> Total: ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.test.__total__", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
tellraw @a [{"text": ">> Passed: ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.test.__pass__", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
tellraw @a [{"text": ">> Failled: ", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"},{"score": { "name": "default.test.__fail__", "objective": "tbms.var"}, "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
