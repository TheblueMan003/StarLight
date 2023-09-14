# ==================================================
# void default.test.TestRunner.onStart-0()
# a.k.a default.test.TestRunner.onStart
# ==================================================

tellraw @a [{"text": "Test started", "bold":"false","obfuscated":"false","strikethrough":"false","underlined":"false","italic":"false", "color":"white"}]
scoreboard players set default.test.TestRunner.index tbms.var -1
scoreboard players set default.test.TestRunner.running tbms.var 0
function default:test/-test-runner/next
