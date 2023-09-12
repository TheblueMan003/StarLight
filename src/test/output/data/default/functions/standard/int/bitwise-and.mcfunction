# ==================================================
# int default.standard.int.bitwiseAnd(int a, int b)
# ==================================================
# ==================================================
# Returns a bitwise and with b. Computation is done using modulo 2 arithmetic.
# ==================================================

scoreboard players set default.standard.int.bitwiseAnd.result tbms.var 0
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2147483647.. if score default.standard.int.bitwiseAnd.a tbms.var matches 2147483647.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 2147483647
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 2147483647.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 2147483647
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2147483647.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 2147483647
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1073741824.. if score default.standard.int.bitwiseAnd.a tbms.var matches 1073741824.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 1073741824
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 1073741824.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 1073741824
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1073741824.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 1073741824
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 536870912.. if score default.standard.int.bitwiseAnd.a tbms.var matches 536870912.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 536870912
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 536870912.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 536870912
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 536870912.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 536870912
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 268435456.. if score default.standard.int.bitwiseAnd.a tbms.var matches 268435456.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 268435456
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 268435456.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 268435456
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 268435456.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 268435456
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 134217728.. if score default.standard.int.bitwiseAnd.a tbms.var matches 134217728.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 134217728
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 134217728.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 134217728
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 134217728.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 134217728
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 67108864.. if score default.standard.int.bitwiseAnd.a tbms.var matches 67108864.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 67108864
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 67108864.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 67108864
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 67108864.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 67108864
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 33554432.. if score default.standard.int.bitwiseAnd.a tbms.var matches 33554432.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 33554432
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 33554432.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 33554432
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 33554432.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 33554432
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 16777216.. if score default.standard.int.bitwiseAnd.a tbms.var matches 16777216.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 16777216
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 16777216.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 16777216
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 16777216.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 16777216
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 8388608.. if score default.standard.int.bitwiseAnd.a tbms.var matches 8388608.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 8388608
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 8388608.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 8388608
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 8388608.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 8388608
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 4194304.. if score default.standard.int.bitwiseAnd.a tbms.var matches 4194304.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 4194304
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 4194304.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 4194304
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 4194304.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 4194304
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2097152.. if score default.standard.int.bitwiseAnd.a tbms.var matches 2097152.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 2097152
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 2097152.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 2097152
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2097152.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 2097152
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1048576.. if score default.standard.int.bitwiseAnd.a tbms.var matches 1048576.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 1048576
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 1048576.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 1048576
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1048576.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 1048576
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 524288.. if score default.standard.int.bitwiseAnd.a tbms.var matches 524288.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 524288
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 524288.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 524288
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 524288.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 524288
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 262144.. if score default.standard.int.bitwiseAnd.a tbms.var matches 262144.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 262144
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 262144.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 262144
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 262144.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 262144
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 131072.. if score default.standard.int.bitwiseAnd.a tbms.var matches 131072.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 131072
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 131072.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 131072
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 131072.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 131072
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 65536.. if score default.standard.int.bitwiseAnd.a tbms.var matches 65536.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 65536
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 65536.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 65536
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 65536.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 65536
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 32768.. if score default.standard.int.bitwiseAnd.a tbms.var matches 32768.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 32768
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 32768.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 32768
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 32768.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 32768
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 16384.. if score default.standard.int.bitwiseAnd.a tbms.var matches 16384.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 16384
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 16384.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 16384
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 16384.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 16384
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 8192.. if score default.standard.int.bitwiseAnd.a tbms.var matches 8192.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 8192
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 8192.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 8192
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 8192.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 8192
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 4096.. if score default.standard.int.bitwiseAnd.a tbms.var matches 4096.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 4096
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 4096.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 4096
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 4096.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 4096
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2048.. if score default.standard.int.bitwiseAnd.a tbms.var matches 2048.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 2048
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 2048.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 2048
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2048.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 2048
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1024.. if score default.standard.int.bitwiseAnd.a tbms.var matches 1024.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 1024
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 1024.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 1024
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1024.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 1024
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 512.. if score default.standard.int.bitwiseAnd.a tbms.var matches 512.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 512
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 512.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 512
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 512.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 512
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 256.. if score default.standard.int.bitwiseAnd.a tbms.var matches 256.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 256
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 256.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 256
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 256.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 256
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 128.. if score default.standard.int.bitwiseAnd.a tbms.var matches 128.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 128
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 128.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 128
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 128.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 128
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 64.. if score default.standard.int.bitwiseAnd.a tbms.var matches 64.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 64
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 64.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 64
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 64.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 64
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 32.. if score default.standard.int.bitwiseAnd.a tbms.var matches 32.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 32
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 32.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 32
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 32.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 32
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 16.. if score default.standard.int.bitwiseAnd.a tbms.var matches 16.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 16
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 16.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 16
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 16.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 16
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 8.. if score default.standard.int.bitwiseAnd.a tbms.var matches 8.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 8
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 8.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 8
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 8.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 8
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 4.. if score default.standard.int.bitwiseAnd.a tbms.var matches 4.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 4
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 4.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 4
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 4.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 4
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2.. if score default.standard.int.bitwiseAnd.a tbms.var matches 2.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 2
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 2.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 2
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 2.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 2
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1.. if score default.standard.int.bitwiseAnd.a tbms.var matches 1.. run scoreboard players add default.standard.int.bitwiseAnd.result tbms.var 1
execute if score default.standard.int.bitwiseAnd.a tbms.var matches 1.. run scoreboard players remove default.standard.int.bitwiseAnd.a tbms.var 1
execute if score default.standard.int.bitwiseAnd.b tbms.var matches 1.. run scoreboard players remove default.standard.int.bitwiseAnd.b tbms.var 1
scoreboard players operation default.standard.int.bitwiseAnd._ret tbms.var = default.standard.int.bitwiseAnd.result tbms.var
