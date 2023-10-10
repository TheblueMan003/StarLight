package standard.char

typedef string char

char toLower(char c) {
    char ret = c
    switch(c){
        "A" -> ret = "a"
        "B" -> ret = "b"
        "C" -> ret = "c"
        "D" -> ret = "d"
        "E" -> ret = "e"
        "F" -> ret = "f"
        "G" -> ret = "g"
        "H" -> ret = "h"
        "I" -> ret = "i"
        "J" -> ret = "j"
        "K" -> ret = "k"
        "L" -> ret = "l"
        "M" -> ret = "m"
        "N" -> ret = "n"
        "O" -> ret = "o"
        "P" -> ret = "p"
        "Q" -> ret = "q"
        "R" -> ret = "r"
        "S" -> ret = "s"
        "T" -> ret = "t"
        "U" -> ret = "u"
        "V" -> ret = "v"
        "W" -> ret = "w"
        "X" -> ret = "x"
        "Y" -> ret = "y"
        "Z" -> ret = "z"
    }
    return ret
}

char toUpper(char c) {
    char ret = c
    switch(c){
        "a" -> ret = "A"
        "b" -> ret = "B"
        "c" -> ret = "C"
        "d" -> ret = "D"
        "e" -> ret = "E"
        "f" -> ret = "F"
        "g" -> ret = "G"
        "h" -> ret = "H"
        "i" -> ret = "I"
        "j" -> ret = "J"
        "k" -> ret = "K"
        "l" -> ret = "L"
        "m" -> ret = "M"
        "n" -> ret = "N"
        "o" -> ret = "O"
        "p" -> ret = "P"
        "q" -> ret = "Q"
        "r" -> ret = "R"
        "s" -> ret = "S"
        "t" -> ret = "T"
        "u" -> ret = "U"
        "v" -> ret = "V"
        "w" -> ret = "W"
        "x" -> ret = "X"
        "y" -> ret = "Y"
        "z" -> ret = "Z"
    }
    return ret
}

int getAscii(char c){
    int ret = 0
    switch(c){
        "\0" -> ret = 0
        "\a" -> ret = 7
        "\b" -> ret = 8
        "\t" -> ret = 9
        "\n" -> ret = 10
        "\v" -> ret = 11
        "\f" -> ret = 12
        "\r" -> ret = 13
        " " -> ret = 32
        "!" -> ret = 33
        "\"" -> ret = 34
        "#" -> ret = 35
        "$" -> ret = 36
        "%" -> ret = 37
        "&" -> ret = 38
        "'" -> ret = 39
        "(" -> ret = 40
        ")" -> ret = 41
        "*" -> ret = 42
        "+" -> ret = 43
        "," -> ret = 44
        "-" -> ret = 45
        "." -> ret = 46
        "/" -> ret = 47
        "0" -> ret = 48
        "1" -> ret = 49
        "2" -> ret = 50
        "3" -> ret = 51
        "4" -> ret = 52
        "5" -> ret = 53
        "6" -> ret = 54
        "7" -> ret = 55
        "8" -> ret = 56
        "9" -> ret = 57
        ":" -> ret = 58
        ";" -> ret = 59
        "<" -> ret = 60
        "=" -> ret = 61
        ">" -> ret = 62
        "?" -> ret = 63
        "@" -> ret = 64
        "A" -> ret = 65
        "B" -> ret = 66
        "C" -> ret = 67
        "D" -> ret = 68
        "E" -> ret = 69
        "F" -> ret = 70
        "G" -> ret = 71
        "H" -> ret = 72
        "I" -> ret = 73
        "J" -> ret = 74
        "K" -> ret = 75
        "L" -> ret = 76
        "M" -> ret = 77
        "N" -> ret = 78
        "O" -> ret = 79
        "P" -> ret = 80
        "Q" -> ret = 81
        "R" -> ret = 82
        "S" -> ret = 83
        "T" -> ret = 84
        "U" -> ret = 85
        "V" -> ret = 86
        "W" -> ret = 87
        "X" -> ret = 88
        "Y" -> ret = 89
        "Z" -> ret = 90
        "[" -> ret = 91
        "]" -> ret = 93
        "^" -> ret = 94
        "_" -> ret = 95
        "`" -> ret = 96
        "a" -> ret = 97
        "b" -> ret = 98
        "c" -> ret = 99
        "d" -> ret = 100
        "e" -> ret = 101
        "f" -> ret = 102
        "g" -> ret = 103
        "h" -> ret = 104
        "i" -> ret = 105
        "j" -> ret = 106
        "k" -> ret = 107
        "l" -> ret = 108
        "m" -> ret = 109
        "n" -> ret = 110
        "o" -> ret = 111
        "p" -> ret = 112
        "q" -> ret = 113
        "r" -> ret = 114
        "s" -> ret = 115
        "t" -> ret = 116
        "u" -> ret = 117
        "v" -> ret = 118
        "w" -> ret = 119
        "x" -> ret = 120
        "y" -> ret = 121
        "z" -> ret = 122
        "{" -> ret = 123
        "|" -> ret = 124
        "}" -> ret = 125
        "~" -> ret = 126
    }
    return ret
}