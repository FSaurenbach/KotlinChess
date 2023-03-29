import com.soywiz.korge.view.Container
import com.soywiz.korge.view.image
import com.soywiz.korge.view.size
import com.soywiz.korim.bitmap.Bitmap
import kotlin.math.abs

class Piece(var color: String, var type: String, pieceX: Int, pieceY: Int, bitmap: Bitmap) : Container() {

    // create a SolidRect object with the tile size and color red to represent the piece
    //private var piece = solidRect(tileSize, tileSize, Colors.RED)
    private var piece = image(bitmap).apply {
        smoothing = false

    }.size(tileSize - 10, tileSize - 10)
    private var pawnFirstMove = false

    // initialize the piece's position on the board
    init {
        pawnFirstMove = type == "pawn"
        piece.pos = boardPosition(pieceX, pieceY)

        // make the piece non-interactive with the mouse
        piece.apply {
            mouseEnabled = false
        }
    }

    // move the piece to a new position if the move is valid
    fun move(x: Int, y: Int, oldX: Int, oldY: Int, type: String, color: String) {

        if (checkMove(x, y, oldX, oldY, type, color)) {
            piece.pos = boardPosition(x, y)
        }
    }

    // check if a move is valid based on the piece's type, color, and the old and new positions
    private fun checkMove(newX: Int, newY: Int, oldX: Int, oldY: Int, type: String, color: String): Boolean {
        println(type)
        when (type) {
            "pawn" -> {
                println("pawn")
                val direction = if (color == "white") 1 else -1
                val oneSquareForward = newY == oldY + direction && newX == oldX
                val twoSquaresForward = pawnFirstMove && newY == oldY + 2 * direction && newX == oldX
                val captureLeft = newX == oldX - 1 && newY == oldY + direction
                val captureRight = newX == oldX + 1 && newY == oldY + direction

                if (oneSquareForward || twoSquaresForward) {
                    pawnFirstMove = false
                    return true
                } else if (captureLeft || captureRight) {
                    val pieces = if (color == "white") blackPieces else whitePieces
                    for (piece in pieces!!) {
                        val (x, y) = deBoardPosition(piece.piece)
                        if (x == newX && y == newY) {
                            piece.removeFromParent()
                            pawnFirstMove = false
                            return true
                        }
                    }
                }
            }

            "rook" -> {
                var canMove = true
                if (newX == oldX && newY != oldY) {
                    val direction = if (newY > oldY) 1 else -1
                    if (direction == -1) {
                        for (i in oldY + direction downTo newY step abs(direction)) {
                            println(i)
                            for (piece in allPieces!!) {

                                val (x, y) = deBoardPosition(piece.piece)
                                if (x == newX && y == newY) {
                                    piece.removeFromParent()
                                    return true
                                }
                                if (x == newX && y == i) return false
                            }
                        }
                    }
                    if (direction == 1) {
                        for (i in oldY + direction until newY step direction) {

                            println(i)
                            for (piece in allPieces!!) {
                                val (x, y) = deBoardPosition(piece.piece)
                                if (x == newX && y == newY) {
                                    println("Rook took a piece Position: x: $newX, y: $newY")
                                    piece.removeFromParent()
                                    return true
                                }

                                if (x == newX && y == i) return false


                            }
                        }
                    }


                }
                /* Nach rechts und links */
                else if (newY == oldY && newX != oldX) {
                    val direction = if (newX > oldX) 1 else -1
                    if (direction == 1) {
                        for (i in oldX + direction until newX step direction) {
                            println("fa $i")
                            for (piece in allPieces!!) {
                                val (x, y) = deBoardPosition(piece.piece)
                                if (x == newX && y == newY) {
                                    println("Rook took a piece Position: x: $newX, y: $newY")
                                    piece.removeFromParent()
                                    return true
                                }
                                if (x == i && y == newY) return false

                            }
                        }
                    }
                    if (direction == -1) {
                        for (i in oldX + direction downTo newX step abs(direction)) {
                            for (piece in allPieces!!) {
                                val (x, y) = deBoardPosition(piece.piece)
                                if (x == newX && y == newY) {
                                    println("Rook took a piece Position: x: $newX, y: $newY")
                                    piece.removeFromParent()
                                    return true
                                }
                                if (x == i && y == newY) return false
                            }
                        }
                    }

                } else if (newX != oldX) {
                    return false
                }

                return true

            }


        }
        // if the move is not valid based on the piece's type, color, and the old and new positions
        return false
    }
}
