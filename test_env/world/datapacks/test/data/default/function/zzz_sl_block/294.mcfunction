# ==================================================
# void default.zzz_sl_block.294()
# a.k.a default.cmd.tp.relativeZ.3
# ==================================================

scoreboard players set default.cmd.tp.tpAxisMinus.x tbms.var 0
scoreboard players operation default.cmd.tp.tpAxisMinus.x tbms.var -= default.cmd.tp.relativeZ.z tbms.var
scoreboard players set default.cmd.tp.tpAxisMinus.axis tbms.var 2
function default:zzz_sl_block/289
