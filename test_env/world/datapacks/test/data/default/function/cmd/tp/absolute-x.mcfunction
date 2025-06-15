# ==================================================
# void default.cmd.tp.absoluteX(float x)
# ==================================================
# ==================================================
# Teleport the currently `x` blocks absolute position on axis x
# ==================================================

execute at @s run tp @s 0.0 ~ ~
scoreboard players operation default.cmd.tp.relativeX.x tbms.var = default.cmd.tp.absoluteX.x tbms.var
function default:cmd/tp/relative-x
