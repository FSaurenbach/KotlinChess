// Import required libraries
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*

// Define variables for the chess pieces, old square, and whether a piece has been clicked
var whitePieces: List<Piece>? = null
var blackPieces: List<Piece>? = null
var oldSquare: Point? = null
var pieceClicked = false
var clickedPiece: Piece? = null

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

                    // Add the tile to the container
                    addChild(tile)
                    // Add an onClick event listener to the tile
                    tile.onClick { tileClicked(tile) }
                }
            }
        }

        // Create two chess pieces and add them to the scene
        val whitePawn = Piece("white", "pawn", 0, 1)
        val blackPawn = Piece("black", "pawn", 1, 6)

        addChild(whitePawn)
        addChild(blackPawn)
        whitePieces = listOf(whitePawn)
        blackPieces = listOf(blackPawn)
    }
}


// This function is called when a tile is clicked
fun tileClicked(tile: SolidRect) {
    val (x, y) = deBoardPosition(tile) // get its board position

    // get the x and y of the previously clicked square if there was one
    val oldX = oldSquare?.x?.div(80)
    val oldY = oldSquare?.y?.div(80)

    /*
    if (oldSquare != null) {
        println("oldX: ${oldSquare!!.x / 80}, oldY: ${oldSquare!!.y / 80}")
    }
    println("x: $x, y: $y")
    println("pieceClicked: $pieceClicked")
    */

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
fun deBoardPosition(tile: SolidRect) = Pair(tile.x.toInt() / tileSize, tile.y.toInt() / tileSize)

// This function converts a board position to a tile
fun boardPosition(x: Int, y: Int) = Point(x * tileSize, y * tileSize)

