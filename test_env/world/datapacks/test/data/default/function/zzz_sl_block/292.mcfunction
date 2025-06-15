# ==================================================
# void default.zzz_sl_block.292()
# a.k.a default.cmd.tp.rotateRelativeX.3
# ==================================================

scoreboard players set default.cmd.tp.tpAxisMinus.x tbms.var 0
scoreboard players operation default.cmd.tp.tpAxisMinus.x tbms.var -= default.cmd.tp.rotateRelativeX.x tbms.var
scoreboard players set default.cmd.tp.tpAxisMinus.axis tbms.var 3
function default:zzz_sl_block/289
