## lazy void cmd.bossbar.create(mcobject $name, rawjson $display)
- mcobject $name
- rawjson $display

Create a bossbar with `name` and text `display`

## lazy void cmd.bossbar.delete(mcobject $name)
- mcobject $name

Delete the bossbar with `name`

## lazy void cmd.bossbar.setColor(mcobject $name, mcobject $color)
- mcobject $name
- mcobject $color

Set the color of bossbar `name` to `color`

## lazy void cmd.bossbar.setMax(mcobject $name, int $value)
- mcobject $name
- int $value

Set the max of bossbar `name` to `value`

## lazy void cmd.bossbar.setValue(mcobject $name, int $value)
- mcobject $name
- int $value

Set the value of bossbar `name` to `value`

## lazy void cmd.bossbar.setValueVar(mcobject $name, int $value)
- mcobject $name
- int $value

Set the value of bossbar `name` to the variable `value`

## lazy void cmd.bossbar.setMaxVar(mcobject $name, int $value)
- mcobject $name
- int $value

Set the max of bossbar `name` to variable `value`

## lazy void cmd.bossbar.setName(mcobject $name, rawjson $value)
- mcobject $name
- rawjson $value

Set the text of bossbar `name` to `value`

## lazy void cmd.bossbar.setVisible(mcobject $name, int $value)
- mcobject $name
- int $value

Set the visibility of bossbar `name` to `value`

## lazy void cmd.bossbar.show(mcobject $name)
- mcobject $name

Set the visibility of bossbar `name` to true

## lazy void cmd.bossbar.showEveryone(mcobject $name)
- mcobject $name

Show the bossbar `name` to every player

## lazy void cmd.bossbar.showPlayer(mcobject $name, entity $player)
- mcobject $name
- entity $player

Show the bossbar `name` to `player`

## lazy void cmd.bossbar.hide(mcobject $name)
- mcobject $name

Hide the bossbar `name`

## lazy void cmd.bossbar.setPlayer(mcobject $name, entity $player)
- mcobject $name
- entity $player

Set the bossbar `name` player to `player`

## lazy void cmd.bossbar.setNotched10(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_10

## lazy void cmd.bossbar.setNotched12(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_12

## lazy void cmd.bossbar.setNotched20(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_20

## lazy void cmd.bossbar.setNotched6(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_6

## lazy void cmd.bossbar.setNotched0(mcobject $name)
- mcobject $name

Set the bossbar `name` style to be notched_0

# struct cmd.bossbar.Bossbar


## lazy void cmd.bossbar.__init__(rawjson value)
- rawjson value

Create a bossbar with text `display`

## lazy void cmd.bossbar.delete()
Delete the bossbar

## lazy void cmd.bossbar.setColor(int value)
- int value

Set the color of bossbar `color`

## lazy void cmd.bossbar.setMax(int value)
- int value

Set the max of bossbar to `value`

## lazy void cmd.bossbar.setValue(int $value)
- int $value

Set the value of bossbar to `value`

## lazy void cmd.bossbar.setValue(int value, int max)
- int value
- int max

Set the value & max of bossbar to `value` & `max`

## lazy void cmd.bossbar.setName(rawjson value)
- rawjson value

Set the text of bossbar `name` to `value`

## lazy void cmd.bossbar.setVisible(bool value)
- bool value

Set the visibility of bossbar to `value`

## lazy void cmd.bossbar.show()
Set the visibility of bossbar to true

## lazy void cmd.bossbar.showEveryone()
Show the bossbar to every player

## lazy void cmd.bossbar.showPlayer(entity player)
- entity player

Show the bossbar `player`

## lazy void cmd.bossbar.hide()
Hide the bossbar

## lazy void cmd.bossbar.setPlayer(entity player)
- entity player

Set the bossbar player to `player`

## lazy void cmd.bossbar.setNotched10()
Set the bossbar style to be notched_10

## lazy void cmd.bossbar.setNotched12()
Set the bossbar style to be notched_12

## lazy void cmd.bossbar.setNotched20()
Set the bossbar style to be notched_20

## lazy void cmd.bossbar.setNotched6()
Set the bossbar style to be notched_6

## lazy void cmd.bossbar.setNotched0()
Set the bossbar style to be notched_0

## @bossbar.shadow void cmd.bossbar.shadow()
Hide all the bossbar

## @bossbar.unshadow void cmd.bossbar.unshadow()
Unhide all the bossbar




