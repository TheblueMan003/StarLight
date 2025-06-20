package sl.MappingTool

import sl.Compilation.BlockConverter

object FromTileMap {
    private val cache = scala.collection.mutable.Map[(String, String, String, String), List[String]]()

    def clear_cache(): Unit = {
        cache.clear()
    }

    def call(map: String, tileset: String, spliter: String, axis: String): List[String] = {
        if (cache.contains((map, tileset, spliter, axis))) {
            return cache((map, tileset, spliter, axis))
        }
        // open file
        val lines = sl.Utils.getFileLines(map)    .toArray
        val tiles = sl.Utils.getFileLines(tileset).toArray

        // get each cell, by splitting by comma
        val cells = lines.map(_.split(spliter).map(_.trim).filterNot(_.size == 0).map(Integer.parseInt))

        val first_axis = axis.charAt(0)
        val second_axis = axis.charAt(1)

        def make_coordinate(x: Int, y: Int): String = {
            val x2 = if (first_axis == 'x') { x } else if (second_axis == 'x') { y } else 0
            val y2 = if (first_axis == 'y') { x } else if (second_axis == 'y') { y } else 0
            val z2 = if (first_axis == 'z') { x } else if (second_axis == 'z') { y } else 0

            s"~$x2 ~$y2 ~$z2"
        }
        def get_tile(i: Int): String = {
            if (i < 0 || i >= tiles.length) {
                throw new IndexOutOfBoundsException(s"Tile index $i is out of bounds for tileset with ${tiles.length} tiles.")
            }
            BlockConverter.get_platform_block_name(tiles(i))
        }

        val ret = 
            for(i <- (0 until cells.length);
                j <- (0 until cells(i).length)) yield {
                val tileIndex = cells(i)(j)
                val tile = get_tile(tileIndex)
                val coordinate = make_coordinate(i, j)
                s"/setblock $coordinate $tile replace"
            }
        
        cache((map, tileset, spliter, axis)) = ret.toList
        ret.toList
    }
}
