# ==================================================
# void default.cmd.tp.absoluteY(float y)
# ==================================================
# ==================================================
# Teleport the currently `y` blocks absolute position on axis y
# ==================================================

execute at @s run tp @s ~ 0.0 ~
scoreboard players operation default.cmd.tp.relativeY.y tbms.var = default.cmd.tp.absoluteY.y tbms.var
function default:cmd/tp/relative-y
