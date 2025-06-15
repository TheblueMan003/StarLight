# ==================================================
# void default.cmd.tp.rotateRelativeY(float y)
# ==================================================
# ==================================================
# Rotate the entity of `y` degree relativly to the current rotation on the y axis
# ==================================================

execute if score default.cmd.tp.rotateRelativeY.y tbms.var matches 1.. run function default:zzz_sl_block/288
execute if score default.cmd.tp.rotateRelativeY.y tbms.var matches ..-1 run function default:zzz_sl_block/290
