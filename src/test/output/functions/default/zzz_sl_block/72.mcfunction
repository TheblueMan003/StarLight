scoreboard players set default.cmd.tp.tpAxisMinus.x tbms.var 0
scoreboard players operation default.cmd.tp.tpAxisMinus.x tbms.var -= default.cmd.tp.relativeY.y tbms.var
scoreboard players set default.cmd.tp.tpAxisMinus.axis tbms.var 1
function default/cmd/tp/tp-axis-minus
