# ==================================================
# void default.cmd.tp.relativeY(float y)
# ==================================================
# ==================================================
# Teleport the currently `y` blocks relativly to the current position on axis y
# ==================================================

execute if score default.cmd.tp.relativeY.y tbms.var matches 1.. run function default:zzz_sl_block/295
execute if score default.cmd.tp.relativeY.y tbms.var matches ..-1 run function default:zzz_sl_block/296
