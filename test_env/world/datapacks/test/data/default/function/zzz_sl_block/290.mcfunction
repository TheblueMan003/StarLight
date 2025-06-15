# ==================================================
# void default.zzz_sl_block.290()
# a.k.a default.cmd.tp.rotateRelativeY.3
# ==================================================

scoreboard players set default.cmd.tp.tpAxisMinus.x tbms.var 0
scoreboard players operation default.cmd.tp.tpAxisMinus.x tbms.var -= default.cmd.tp.rotateRelativeY.y tbms.var
scoreboard players set default.cmd.tp.tpAxisMinus.axis tbms.var 4
function default:zzz_sl_block/289
