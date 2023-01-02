## @playertick void game.room.player()


## @playertick void game.room.player()


## @tick [tag.order=-100] void game.room.room_detection_init()
Room detection init

## @tick [tag.order=100] void game.room.room_detection_end()
Room detection end

## @test.after void game.room.show()
Show the list of active room

# template game.room.Room


## lazy void game.room.init(int sx, int sy, int sz, int ex, int ey, int ez)
- int sx
- int sy
- int sz
- int ex
- int ey
- int ez



## void game.room.setColor(Color? c)
- Color? c

Set the color to display in creative

## void game.room.onEnter()
Call back when a player enter the room.

## void game.room.onStay()
Call back when a player stays in the room.

## void game.room.onActivate()
Call back when the room get activated. A player enter the room while nobody is in it.

## void game.room.onDesactivate()
Call back when the room get desactivated. All the players left the room.

## void game.room.main()
Call back when the room contains at least one player.

## void game.room.onExit()
Call back when a player exit the room.

## private ticking void game.room.__main__()


## private void game.room.particule()


## @game.room.show void game.room.show()


## @game.room.init void game.room.main_init()


## @game.room.end void game.room.main_end()


## private lazy bool game.room.check()


## @game.room.tick [Compile.order=100] void game.room.__main_player__()


## @room.count void game.room.__count__()
Count the number of active room




