# ==================================================
# void default.cmd.tp.relativeZ(float z)
# ==================================================
# ==================================================
# Teleport the currently `z` blocks relativly to the current position on axis z
# ==================================================

execute if score default.cmd.tp.relativeZ.z tbms.var matches 1.. run function default:zzz_sl_block/293
execute if score default.cmd.tp.relativeZ.z tbms.var matches ..-1 run function default:zzz_sl_block/294
