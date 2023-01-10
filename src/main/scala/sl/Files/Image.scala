package sl.files

object Image{

}
class Image(val width: Int, val height: Int) {
    val image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB)

    // Implicite conversion from (Int, Int, Int) to Int
    implicit def rgbToInt(rgb: (Int, Int, Int)): Int = (rgb._1 << 16) + (rgb._2 << 8) + rgb._3
    implicit def rgbaToInt(rgba: (Int, Int, Int, Int)): Int = (rgba._1 << 24) + (rgba._2 << 16) + (rgba._3 << 8) + rgba._4

    def save(filePath: String)={
        val file = new java.io.File(filePath)
        javax.imageio.ImageIO.write(image, "png", file)
        this
    }

    def setPixel(x: Int, y: Int, color: Int) = {
        image.setRGB(x, y, color)
        this
    }

    def drawRectangle(startX: Int, startY: Int, width: Int, height: Int, color: Int) = {
        for (x <- startX until startX + width; y <- startY until startY + height) setPixel(x, y, color)
        this
    }
    def drawCircle(centerX: Int, centerY: Int, radiusX: Int, radiusY: Int, color: Int) = {
        for (x <- centerX - radiusX until centerX + radiusX; y <- centerY - radiusY until centerY + radiusY) {
            if (Math.pow(x - centerX, 2) / Math.pow(radiusX, 2) + Math.pow(y - centerY, 2) / Math.pow(radiusY, 2) <= 1) setPixel(x, y, color)
        }
        this
    }
    def drawLine(startX: Int, startY: Int, endX: Int, endY: Int, color: Int) = {
        val dx = endX - startX
        val dy = endY - startY
        val length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)).toInt
        for (i <- 0 until length) {
            val x = startX + dx * i / length
            val y = startY + dy * i / length
            setPixel(x, y, color)
        }
        this
    }
    def drawText(x: Int, y:Int, text: String, font: String, size: Int, color: Int) = {
        val g = image.getGraphics()
        g.setFont(new java.awt.Font(font, java.awt.Font.PLAIN, size))
        g.setColor(new java.awt.Color(color))
        g.drawString(text, x, y)
        this
    }
    def paste(other: Image, x: Int, y: Int) = {
        for (i <- 0 until other.width; j <- 0 until other.height) {
            val color = other.image.getRGB(i, j)
            if (color != 0) setPixel(x + i, y + j, color)
        }
        this
    }
    def crop(x: Int, y: Int, width: Int, height: Int) = {
        val newImage = new Image(width, height)
        for (i <- 0 until width; j <- 0 until height) {
            newImage.setPixel(i, j, image.getRGB(x + i, y + j))
        }
        newImage
    }
    def resize(width: Int, height: Int) = {
        val newImage = new Image(width, height)
        for (i <- 0 until width; j <- 0 until height) {
            newImage.setPixel(i, j, image.getRGB(i * this.width / width, j * this.height / height))
        }
        newImage
    }
    def getPixel(x: Int, y: Int) = image.getRGB(x, y)
    def applyFilter(filter: (Int, Int, Int, Int)=>Int) = {
        for (i <- 0 until width; j <- 0 until height) {
            val color = image.getRGB(i, j)
            val alpha = (color >> 24) & 0xFF
            val red = (color >> 16) & 0xFF
            val green = (color >> 8) & 0xFF
            val blue = color & 0xFF
            setPixel(i, j, filter(alpha, red, green, blue))
        }
        this
    }
    def grayScale() = applyFilter((alpha, red, green, blue) => (alpha << 24) + (red + green + blue) / 3 * 0x10101)
    def invert() = applyFilter((alpha, red, green, blue) => (alpha << 24) + (255 - red) * 0x10000 + (255 - green) * 0x100 + (255 - blue))
}