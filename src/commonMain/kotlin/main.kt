import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.lang.cancel
import com.soywiz.korma.geom.Point

var oldSquare: Point? = null
var pieceClicked = false // whether a piece has been clicked
var clickedPiece: Piece? = null // the piece that has been clicked
var SceneContainer: SceneContainer? = null // the scene container
const val boardSize = 8 // the size of the board (8x8)
const val tileSize = 80 // the size of each tile in pixels
suspend fun main() = Korge(width = 640, height = 640, bgcolor = Colors["#2b2b2b"]) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MyScene() })
}

class MyScene : Scene() {

    override suspend fun SContainer.sceneMain() {


        // create the chess board
        container().apply {
            // create the tiles
            for (i in 0 until boardSize) {
                for (j in 0 until boardSize) {
                    val tileColor = if ((i + j) % 2 == 0) Colors.WHITE else Colors.BLACK
                    val tile = solidRect(tileSize, tileSize, tileColor)
                    tile.position(tileSize * j, tileSize * i)

                    addChild(tile)
                    tile.onClick { tileClicked(tile) }
                }
            }
        }
        val p = Piece("white", "pawn", 0, 0)
        var p2 = Piece("black", "pawn", 0, 5)
        addChild(p)
        addChild(p2)
    }
}

fun tileClicked(tile: SolidRect) {
    val (x, y) = deBoardPosition(tile) // get its board position
    tile.x
    tile.y
    if (oldSquare != null) {
        println("oldX: ${oldSquare!!.x/80}, oldY: ${oldSquare!!.y/80}")
    }
    println("x: $x, y: $y")
    println("pieceClicked: $pieceClicked")
    /* A piece has been clicked already and the user has clicked a tile to move on.
    The move function will now check if the move is valid*/
    if (pieceClicked && (oldSquare != BoardPosition(x, y))) {
        println("condition 1")
        clickedPiece!!.move(x, y, tile)
        pieceClicked = false
        clickedPiece = null
    }
    /* A piece has not been clicked yet, so the user is trying to select a piece*/
    else {
        tile.onCollision {
            if (it is Piece) {

                println("cond 2")
                pieceClicked = true
                clickedPiece = it
                oldSquare = BoardPosition(x, y)
            }

        }.cancel()
    }
    /*else {
        // check if there is a piece at the clicked tile
        tile.onCollision {
            println("cond 2")
            if (it is Piece) {
                pieceClicked = true
                clickedPiece = it
                oldSquare = BoardPosition(x, y)
            }

        }.cancel()
    }*/
}

// do the opposite of BoardPosition
fun deBoardPosition(tile: SolidRect): Pair<Int, Int> {
    val x = tile.x / tileSize
    val y = tile.y / tileSize
    return Pair(x.toInt(), y.toInt())
}

fun BoardPosition(x: Int, y: Int): Point {
    val x = x * tileSize
    val y = y * tileSize
    return Point(x, y)

}

class Piece(var color: String, var type: String, pieceX:Int, pieceY:Int) : Container() {
    private var piece: SolidRect = solidRect(80, 80, Colors.RED)

    init {
        piece.pos = BoardPosition(pieceX, pieceY)
        piece.apply {
            mouseEnabled = false
        }
    }

    // Move the piece to a new position on the board
    fun move(x: Int, y: Int, tile: SolidRect) {
        val (oldX, oldY) = deBoardPosition(piece)

        if (checkMove(x, y, oldX, oldY, type, color)) {


            piece.pos = BoardPosition(x, y)
        }
    }


    private fun checkMove(newX: Int, newY: Int, oldX: Int, oldY: Int, type: String, color: String): Boolean {

        println("cofadsfdsfnd 2")
        println(type)
        println(color)
        when (type) {
            "pawn" -> {
                if (color == "white") {
                    println("case")

                    if (newX == oldX && newY == oldY + 1) {
                        return true
                    }
                } else {
                    if (newX == oldX && newY == oldY - 1) {
                        return true
                    }
                }
            }
            "rook" -> {
                if (newX == oldX || newY == oldY) {
                    return true
                }
            }
        }
        return false
    }


}
