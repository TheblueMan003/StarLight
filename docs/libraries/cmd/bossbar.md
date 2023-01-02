## lazy void create(mcobject $name, rawjson $display)
- mcobject $name
- rawjson $display

Create a bossbar with `name` and text `display`

## lazy void delete(mcobject $name)
- mcobject $name

Delete the bossbar with `name`

## lazy void setColor(mcobject $name, mcobject $color)
- mcobject $name
- mcobject $color

Set the color of bossbar `name` to `color`

## lazy void setMax(mcobject $name, int $value)
- mcobject $name
- int $value

Set the max of bossbar `name` to `value`

## lazy void setValue(mcobject $name, int $value)
- mcobject $name
- int $value

Set the value of bossbar `name` to `value`

## lazy void setValueVar(mcobject $name, int $value)
- mcobject $name
- int $value

Set the value of bossbar `name` to the variable `value`

## lazy void setMaxVar(mcobject $name, int $value)
- mcobject $name
- int $value

Set the max of bossbar `name` to variable `value`

## lazy void setName(mcobject $name, rawjson $value)
- mcobject $name
- rawjson $value

Set the text of bossbar `name` to `value`

## lazy void setVisible(mcobject $name, int $value)
- mcobject $name
- int $value

Set the visibility of bossbar `name` to `value`

## lazy void show(mcobject $name)
- mcobject $name

Set the visibility of bossbar `name` to true

## lazy void showEveryone(mcobject $name)
- mcobject $name

Show the bossbar `name` to every player

## lazy void showPlayer(mcobject $name, entity $player)
- mcobject $name
- entity $player

Show the bossbar `name` to `player`

## lazy void hide(mcobject $name)
- mcobject $name

Hide the bossbar `name`

## lazy void setPlayer(mcobject $name, entity $player)
- mcobject $name
- entity $player

Set the bossbar `name` player to `player`

## lazy void setNotched10(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_10

## lazy void setNotched12(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_12

## lazy void setNotched20(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_20

## lazy void setNotched6(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_6

## lazy void setNotched0(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_0

# struct Bossbar


## lazy void __init__(rawjson value)
- rawjson value

Create a bossbar with text `display`

## lazy void delete()
Delete the bossbar

## lazy void setColor(int value)
- int value

Set the color of bossbar `color`

## lazy void setMax(int value)
- int value

Set the max of bossbar to `value`

## lazy void setValue(int $value)
- int $value

Set the value of bossbar to `value`

## lazy void setValue(int value, int max)
- int value
- int max

Set the value & max of bossbar to `value` & `max`

## lazy void setName(rawjson value)
- rawjson value

Set the text of bossbar `name` to `value`

## lazy void setVisible(bool value)
- bool value

Set the visibility of bossbar to `value`

## lazy void show()
Set the visibility of bossbar to true

## lazy void showEveryone()
Show the bossbar to every player

## lazy void showPlayer(entity player)
- entity player

Show the bossbar `player`

## lazy void hide()
Hide the bossbar

## lazy void setPlayer(entity player)
- entity player

Set the bossbar player to `player`

## lazy void setNotched10()
Set the bossbar style to be notched_10

## lazy void setNotched12()
Set the bossbar style to be notched_12

## lazy void setNotched20()
Set the bossbar style to be notched_20

## lazy void setNotched6()
Set the bossbar style to be notched_6

## lazy void setNotched0()
Set the bossbar style to be notched_0

## @bossbar.shadow void shadow()
Hide all the bossbar

## @bossbar.unshadow void unshadow()
Unhide all the bossbar




