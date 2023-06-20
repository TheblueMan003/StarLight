import test

align("xyz")./say hi
rotated(0,0)./say hi
rotated(@p)./say hi
facing(~ ~ ~)./say hi
facing(@p)./say hi
facing(@p, "eyes")./say hi
facing(@p, "feet")./say hi
at(~ ~ ~)./say hi
at(@p)./say hi
as(@s)./say hi
with(@a)./say hi
with(@a,true)./say hi
with(@a,false)./say hi
bool b
with(@a,b)./say hi
with(@a,b,b)./say hi

// Selector Test
lazy int a = 3
as(@s[distance=a])./say hi
as(@s[r=a])./say hi
as(@s[distance=..a])./say hi

at(1 ~ ~)./say hi
at(~ 1 ~)./say hi
at(~ ~ 1)./say hi

at(-1 ~ ~)./say hi
at(~ -1 ~)./say hi
at(~ ~ -1)./say hi

at(1 ~ 1)./say hi
at(-1 ~ -1)./say hi