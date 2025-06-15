# ==================================================
# void default.zzz_sl_block.299()
# a.k.a default.cmd.tp.tp-1.1
# ==================================================

scoreboard players operation default.cmd.tp.absolute.x tbms.var = default.cmd.tp.tp-1.x tbms.var
scoreboard players operation default.cmd.tp.absolute.y tbms.var = default.cmd.tp.tp-1.y tbms.var
scoreboard players operation default.cmd.tp.absolute.z tbms.var = default.cmd.tp.tp-1.z tbms.var
tp @s 0.0 0.0 0.0
scoreboard players operation default.cmd.tp.relativeX.x tbms.var = default.cmd.tp.absolute.x tbms.var
function default:cmd/tp/relative-x
scoreboard players operation default.cmd.tp.relativeY.y tbms.var = default.cmd.tp.absolute.y tbms.var
function default:cmd/tp/relative-y
scoreboard players operation default.cmd.tp.relativeZ.z tbms.var = default.cmd.tp.absolute.z tbms.var
function default:cmd/tp/relative-z
