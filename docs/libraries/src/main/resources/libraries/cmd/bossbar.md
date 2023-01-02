## create
- mcobject $name
- rawjson $display
Create a bossbar with `name` and text `display`
## delete
- mcobject $name
Delete the bossbar with `name`
## setColor
- mcobject $name
- mcobject $color
Set the color of bossbar `name` to `color`
## setMax
- mcobject $name
- int $value
Set the max of bossbar `name` to `value`
## setValue
- mcobject $name
- int $value
Set the value of bossbar `name` to `value`
## setValueVar
- mcobject $name
- int $value
Set the value of bossbar `name` to the variable `value`
## setMaxVar
- mcobject $name
- int $value
Set the max of bossbar `name` to variable `value`
## setName
- mcobject $name
- rawjson $value
Set the text of bossbar `name` to `value`
## setVisible
- mcobject $name
- int $value
Set the visibility of bossbar `name` to `value`
## show
- mcobject $name
Set the visibility of bossbar `name` to true
## showEveryone
- mcobject $name
Show the bossbar `name` to every player
## showPlayer
- mcobject $name
- entity $player
Show the bossbar `name` to `player`
## hide
- mcobject $name
Hide the bossbar `name`
## setPlayer
- mcobject $name
- entity $player
Set the bossbar `name` player to `player`
## setNotched10
- mcobject $name
Set the bossbar `name` style to be notched_10
## setNotched12
- mcobject $name
Set the bossbar `name` style to be notched_12
## setNotched20
- mcobject $name
Set the bossbar `name` style to be notched_20
## setNotched6
- mcobject $name
Set the bossbar `name` style to be notched_6
## setNotched0
- mcobject $name
Set the bossbar `name` style to be notched_0
# Bossbar



## __init__
- rawjson value
Create a bossbar with text `display`
## delete

Delete the bossbar
## setColor
- int value
Set the color of bossbar `color`
## setMax
- int value
Set the max of bossbar to `value`
## setValue
- int $value
Set the value of bossbar to `value`
## setValue
- int value
- int max
Set the value & max of bossbar to `value` & `max`
## setName
- rawjson value
Set the text of bossbar `name` to `value`
## setVisible
- bool value
Set the visibility of bossbar to `value`
## show

Set the visibility of bossbar to true
## showEveryone

Show the bossbar to every player
## showPlayer
- entity player
Show the bossbar `player`
## hide

Hide the bossbar
## setPlayer
- entity player
Set the bossbar player to `player`
## setNotched10

Set the bossbar style to be notched_10
## setNotched12

Set the bossbar style to be notched_12
## setNotched20

Set the bossbar style to be notched_20
## setNotched6

Set the bossbar style to be notched_6
## setNotched0

Set the bossbar style to be notched_0
## shadow

Hide all the bossbar
## unshadow

Unhide all the bossbar
