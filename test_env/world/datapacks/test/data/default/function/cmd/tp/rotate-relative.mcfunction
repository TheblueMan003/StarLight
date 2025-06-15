# ==================================================
# void default.cmd.tp.rotateRelative(float x, float y, float z)
# ==================================================
# ==================================================
# Rotate the current entity of `x`, `y` relativly to the current rotation
# ==================================================

scoreboard players operation default.cmd.tp.rotateRelativeX.x tbms.var = default.cmd.tp.rotateRelative.x tbms.var
function default:cmd/tp/rotate-relative-x
scoreboard players operation default.cmd.tp.rotateRelativeY.y tbms.var = default.cmd.tp.rotateRelative.y tbms.var
function default:cmd/tp/rotate-relative-y
