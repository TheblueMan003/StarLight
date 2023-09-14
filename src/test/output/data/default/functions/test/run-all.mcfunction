# ==================================================
# void default.test.runAll()
# ==================================================

scoreboard players set default.test.__pass__ tbms.var 0
scoreboard players set default.test.__fail__ tbms.var 0
scoreboard players set default.test.__total__ tbms.var 0
function default:test/-test-runner/start
