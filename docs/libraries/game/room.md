## ` @playertick void player()`


## ` @playertick void player()`


## ` @tick [tag.order=-100] void room_detection_init()`
Room detection init

## ` @tick [tag.order=100] void room_detection_end()`
Room detection end

## ` @test.after void show()`
Show the list of active room

# template Room


## ` lazy void init(int sx, int sy, int sz, int ex, int ey, int ez)`


## ` void setColor(Color? c)`
Set the color to display in creative

## ` void onEnter()`
Call back when a player enter the room.

## ` void onStay()`
Call back when a player stays in the room.

## ` void onActivate()`
Call back when the room get activated. A player enter the room while nobody is in it.

## ` void onDesactivate()`
Call back when the room get desactivated. All the players left the room.

## ` void main()`
Call back when the room contains at least one player.

## ` void onExit()`
Call back when a player exit the room.

## ` @game.room.show void show()`


## ` @game.room.init void main_init()`


## ` @game.room.end void main_end()`


## ` @game.room.tick [Compile.order=100] void __main_player__()`


## ` @room.count void __count__()`
Count the number of active room




