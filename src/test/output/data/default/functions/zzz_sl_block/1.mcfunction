# ==================================================
# void default.zzz_sl_block.1()
# a.k.a default.test.test.--async_while--.0
# ==================================================

scoreboard players set default.test.test.-setup-loop- tbms.var 1
scoreboard players operation default.test.test.--async_while--.-exit-loop- tbms.var = default.test.test.--async_while--.--await_callback-- tbms.var
