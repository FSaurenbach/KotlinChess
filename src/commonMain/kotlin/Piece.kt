import com.soywiz.korge.view.Container
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors

class Piece(var color: String, var type: String, pieceX: Int, pieceY: Int) : Container() {

    // create a SolidRect object with the tile size and color red to represent the piece
    private var piece: SolidRect = solidRect(tileSize, tileSize, Colors.RED)
    private var pawnFirstMove = false
    // initialize the piece's position on the board
    init {
        if (type == "pawn"){
            pawnFirstMove = true
        }
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

        when (type) {
            "pawn" -> {
                // if the piece is a pawn and its color is white
                if (color == "white") {
                    if (newX == oldX && newY == oldY + 1) {
                        // if the pawn is moving one square forward
                        pawnFirstMove = false
                        return true
                    }
                    else if (pawnFirstMove && newX == oldX && newY == oldY + 2) {
                        // if the pawn is moving two squares forward
                        pawnFirstMove = false
                        return true
                    }

                    else if ((newX == oldX + 1 && newY == oldY + 1) || (newX == oldX -1 && newY == oldY +1)) {
                        // if the pawn is capturing a black piece diagonally
                        for (piece in blackPieces!!) {
                            val (x, y) = deBoardPosition(piece.piece)
                            if (x == newX && y == newY) {
                                // remove the captured piece from the board
                                piece.removeFromParent()
                                pawnFirstMove = false
                                return true
                            }
                        }
                    }
                }
                else if (color == "black") {
                    if (newX == oldX && newY == oldY - 1) {
                        // if the pawn is moving one square forward
                        pawnFirstMove = false
                        return true
                    }
                    else if (pawnFirstMove && newX == oldX && newY == oldY - 2) {
                        // if the pawn is moving two squares forward
                        pawnFirstMove = false
                        return true
                    }
                    else if ((newX == oldX + 1 && newY == oldY - 1) || (newX == oldX -1 && newY == oldY -1)) {
                        // if the pawn is capturing a white piece diagonally
                        for (piece in whitePieces!!) {
                            val (x, y) = deBoardPosition(piece.piece)
                            if (x == newX && y == newY) {
                                // remove the captured piece from the board
                                piece.removeFromParent()
                                pawnFirstMove = false
                                return true
                            }
                        }
                    }
                }
            }
        }
        // if the move is not valid based on the piece's type, color, and the old and new positions
        return false
    }
}
