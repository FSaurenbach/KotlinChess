// Import required libraries
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.cancel
import com.soywiz.korma.geom.Point
import kotlin.collections.List
import kotlin.collections.listOf
import kotlin.collections.mutableMapOf
import kotlin.collections.plus
import kotlin.collections.set

// Define variables for the chess pieces, old square, and whether a piece has been clicked
var whitePieces: List<Piece>? = null
var blackPieces: List<Piece>? = null
var allPieces: List<Piece>? = null
var oldSquare: Point? = null
var pieceClicked = false
var clickedPiece: Piece? = null
val rectsBoard = mutableMapOf<Pair<Int, Int>, View>()

// Define constants for the board size and tile size
const val boardSize = 8
const val tileSize = 80

// Define the main function
suspend fun main() = Korge(width = 640, height = 640, bgcolor = Colors["#2b2b2b"]) {
    // Create a scene container
    val sceneContainer = sceneContainer()

    // Set the scene to an instance of MyScene
    sceneContainer.changeTo({ MyScene() })
}

// Define the MyScene class
class MyScene : Scene() {
    // Override the sceneMain function to define the main content of the scene
    override suspend fun SContainer.sceneMain() {
        // Create the chess board
        container().apply {
            // Create the tiles
            for (i in 0 until boardSize) {
                for (j in 0 until boardSize) {
                    // Set the tile color based on its position
                    val tileColor = if ((i + j) % 2 == 0) Colors.WHITE else Colors.BLACK
                    // Create the tile
                    val tile = solidRect(tileSize, tileSize, tileColor)
                    // Set the tile position
                    tile.position(tileSize * j, tileSize * i)
                    rectsBoard[j to i] = tile
                    val letters2 = "01234567"
                    for (b in 0..7) {
                        text(
                            "${letters2[b]}",
                            textSize = 25.0,
                            color = if (b % 2 != 0) Colors.BLACK else Colors.WHITE
                        ).xy(
                            8.0 * tileSize - 15.0, 1.0 + b * tileSize
                        ).addTo(this)
                    }
                    val letters = "01234567"
                    for (b2 in 0..7) {
                        text(letters[b2] + "", textSize = 25.0, color = if (b2 % 2 != 0) Colors.BLACK else Colors.WHITE)

                            .xy(3.0 + b2 * tileSize, 8 * tileSize - 30.0).addTo(this)
                    }
                    // Add the tile to the container
                    addChild(tile)
                    // Add an onClick event listener to the tile
                    tile.onClick { tileClicked(tile) }
                }
            }
        }

        // Create two chess pieces and add them to the scene
        val whitePawn = Piece("white", "pawn", 0, 1, resourcesVfs["whitePawn.png"].readBitmap())
        val blackPawn = Piece("black", "pawn", 3, 6, resourcesVfs["whitePawn.png"].readBitmap())
        val whiteRook = Piece("white", "rook", 0, 0, resourcesVfs["whitePawn.png"].readBitmap())
        val blackRook = Piece("black", "rook", 7, 7, resourcesVfs["whitePawn.png"].readBitmap())
        addChild(whitePawn)
        addChild(blackPawn)
        addChild(whiteRook)
        addChild(blackRook)
        whitePieces = listOf(whitePawn, whiteRook)
        blackPieces = listOf(blackPawn, blackRook)
        allPieces = whitePieces!! + blackPieces!!
    }
}


// This function is called when a tile is clicked
fun tileClicked(tile: SolidRect) {
    val (x, y) = deBoardPosition(tile) // get its board position

    // get the x and y of the previously clicked square if there was one
    val oldX = oldSquare?.x?.div(80)
    val oldY = oldSquare?.y?.div(80)


    /* A piece has been clicked already and the user has clicked a tile to move on.
    The move function will now check if the move is valid*/
    if (pieceClicked && (oldSquare != boardPosition(x, y))) {
        println(
            "condition 1 color: ${clickedPiece!!.color}, type: ${clickedPiece!!.type}, x: ${oldX}, y: ${oldY}, " + "x: $x , y: $y"
        )

        // Call the move function of the clicked piece
        clickedPiece!!.move(x, y, oldX!!.toInt(), oldY!!.toInt(), clickedPiece!!.type, clickedPiece!!.color)

        // reset flags for pieceClicked and clickedPiece
        pieceClicked = false
        clickedPiece = null
    }/* A piece has not been clicked yet, so the user is trying to select a piece*/
    else if (!pieceClicked) {
        var collision = false

        // Add a collision listener to the clicked tile
        tile.onCollision {
            if (it is Piece && !collision) {
                collision = true
                println(
                    "condition 2 color: ${it.color}, type: ${it.type}, x: ${oldX}, y: ${oldY}, " + "x: $x , y: $y"
                )

                // Set flags for pieceClicked and clickedPiece and set oldSquare to the clicked square
                pieceClicked = true
                clickedPiece = it
                oldSquare = boardPosition(x, y)
            }

            // cancel the collision listener to avoid multiple clicks on the same piece
        }.cancel()
    }
}

// This function converts a tile to its board position
fun deBoardPosition(tile: View) = Pair(tile.x.toInt() / tileSize, tile.y.toInt() / tileSize)

// This function converts a board position to a tile
fun boardPosition(x: Int, y: Int) = Point(x * tileSize, y * tileSize)

