# ==================================================
# void default.zzz_sl_block.371(int start, int end)
# a.k.a default.standard.string.slice.inner
# ==================================================

$data modify storage default.standard.string.slice._ret json set string storage default.standard.string.slice.source json $(start) $(end)
