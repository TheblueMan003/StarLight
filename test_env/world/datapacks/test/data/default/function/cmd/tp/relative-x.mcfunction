# ==================================================
# void default.cmd.tp.relativeX(float x)
# ==================================================
# ==================================================
# Teleport the currently `x` blocks relativly to the current position on axis x
# ==================================================

execute if score default.cmd.tp.relativeX.x tbms.var matches 1.. run function default:zzz_sl_block/297
execute if score default.cmd.tp.relativeX.x tbms.var matches ..-1 run function default:zzz_sl_block/298
