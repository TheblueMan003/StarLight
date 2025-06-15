# ==================================================
# void default.cmd.tp.rotateRelativeX(float x)
# ==================================================
# ==================================================
# Rotate the entity of `x` degree relativly to the current rotation on the x axis
# ==================================================

execute if score default.cmd.tp.rotateRelativeX.x tbms.var matches 1.. run function default:zzz_sl_block/291
execute if score default.cmd.tp.rotateRelativeX.x tbms.var matches ..-1 run function default:zzz_sl_block/292
