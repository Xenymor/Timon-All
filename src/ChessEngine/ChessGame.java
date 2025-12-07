package ChessEngine;

import ChessEngine.ChessBoard.Move;
import ChessEngine.ChessBoard.ScoreAndMove;

import java.util.*;

public class ChessGame {
    public static final int depth = 3;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ChessBoard board = new ChessBoard();
        board.initializeBoard();

        while (!board.isGameOver()) {
            System.out.println(board);

            if (board.whiteOnTurn) {
                System.out.print("White's move: ");
                Move move = board.parseMove(sc.nextLine());
                if (!board.isValidMove(move.fromRow(), move.fromCol(), move.toRow(), move.toCol())) {
                    System.out.println("Invalid move, try again.");
                    continue;
                }
                board.makeMove(move);
            } else {
                ScoreAndMove bestMove = board.minimax(3, false);
                System.out.println("Black's move: " + bestMove + "; Score: " + bestMove.score());
                board.makeMove(bestMove.move());
            }
        }

        System.out.println(board);
        System.out.println("Game over.");
    }
}


enum PieceType {
    EMPTY, PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

class Piece {
    final PieceType type;
    final boolean isWhite;
    int row, col;

    Piece(PieceType type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }
}

class ChessBoard {
    private static final int BOARD_SIZE = 8;
    boolean whiteOnTurn = true;  // WHITE moves first
    final Piece[][] board;
    private int halfMoveClock = 0;

    ChessBoard() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new Piece(PieceType.EMPTY, false);
            }
        }

        // Place the pawns
        for (int i = 0; i < BOARD_SIZE; i++) {
            board[1][i] = new Piece(PieceType.PAWN, true);
            board[6][i] = new Piece(PieceType.PAWN, false);
        }

        // Place the other pieces
        placePieces(0, false);

        placePieces(7, true);
    }

    List<Move> getLegalMoves(int fromRow, int fromCol) {
        List<Move> legalMoves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isValidMove(fromRow, fromCol, row, col)) {
                    legalMoves.add(new Move(fromRow, fromCol, row, col, board[row][col], board));
                }
            }
        }
        return legalMoves;
    }

    record Move(int fromRow, int fromCol, int toRow, int toCol, Piece capturedPiece, Piece[][] board) {
            @Override
            public String toString() {
                return "Move{" +
                        "fromRow=" + fromRow +
                        ", fromCol=" + fromCol +
                        ", toRow=" + toRow +
                        ", toCol=" + toCol +
                        ", capturedPiece=" + capturedPiece +
                        ", board=" + Arrays.toString(board) +
                        '}';
            }

            Move(int fromRow, int fromCol, int toRow, int toCol, Piece capturedPiece, Piece[][] board) {
                this.fromRow = fromRow;
                this.fromCol = fromCol;
                this.toRow = toRow;
                this.toCol = toCol;
                this.capturedPiece = capturedPiece;
                this.board = new Piece[8][8];
                for (int row = 0; row < 8; row++) {
                    System.arraycopy(board[row], 0, this.board[row], 0, 8);
                }
            }
        }

    public Move parseMove(String moveString) {
        int fromRow, fromCol, toRow, toCol;
        String[] digits = moveString.split("[-,]");
        fromCol = Integer.parseInt(digits[0])-1;
        fromRow = Integer.parseInt(digits[1])-1;
        toCol = Integer.parseInt(digits[2])-1;
        toRow = Integer.parseInt(digits[3])-1;
        return new Move(fromRow, fromCol, toRow, toCol, board[toCol][toRow], board);
    }

    final Stack<Move> movesMade = new Stack<>();
    private final List<Move> moveHistory = new ArrayList<>();

    @SuppressWarnings("UnusedReturnValue")
    public boolean makeMove(Move move) {
        moveHistory.add(move);
        if (!isValidMove(move.fromRow, move.fromCol, move.toRow, move.toCol)) {
            return false;
        }
        Piece piece = board[move.fromRow][move.fromCol];
        board[move.fromRow][move.fromCol] = new Piece(PieceType.EMPTY, true);
        board[move.toRow][move.toCol] = piece;

        // Reset the halfMoveClock if a pawn is moved or a piece is captured.
        if (piece.type == PieceType.PAWN || board[move.toRow][move.toCol].type != PieceType.EMPTY) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        whiteOnTurn = !whiteOnTurn;
        return true;
    }


    void undoMove(Move move) {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.getLast();
            if (lastMove.equals(move)) {
                moveHistory.remove(lastMove);
            }
        }
        Piece toPiece = board[move.toRow][move.toCol];
        board[move.fromRow][move.fromCol] = toPiece;
        board[move.toRow][move.toCol] = move.capturedPiece;
        if (!movesMade.isEmpty()) {
            movesMade.pop();
        }
    }


    private void placePieces(int row, boolean isWhite) {
        board[row][0] = new Piece(PieceType.ROOK, isWhite);
        board[row][1] = new Piece(PieceType.KNIGHT, isWhite);
        board[row][2] = new Piece(PieceType.BISHOP, isWhite);
        board[row][3] = new Piece(PieceType.QUEEN, isWhite);
        board[row][4] = new Piece(PieceType.KING, isWhite);
        board[row][5] = new Piece(PieceType.BISHOP, isWhite);
        board[row][6] = new Piece(PieceType.KNIGHT, isWhite);
        board[row][7] = new Piece(PieceType.ROOK, isWhite);
    }

    boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isWhite) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // Check if the pawn is moving in the right direction
        if (isWhite && rowDiff < 0) {
            return false;
        }
        if (!isWhite && rowDiff > 0) {
            return false;
        }

        // Check if the pawn is making a legal move
        if (Math.abs(colDiff) > 1) {
            // Pawn can only move one column over if it's taking a piece
            if (Math.abs(rowDiff) != 1) {
                return false;
            }
            return board[toRow][toCol].type != PieceType.EMPTY;
        } else if (Math.abs(colDiff) == 1) {
            // Pawn can only move one row forward if it's taking a piece
            if (Math.abs(rowDiff) != 1) {
                return false;
            }
            return board[toRow][toCol].type != PieceType.EMPTY;
        } else {
            // Pawn can move one or two rows forward
            if (Math.abs(rowDiff) > 2) {
                return false;
            }
            if (Math.abs(rowDiff) == 2) {
                if (fromRow != 1 && fromRow != 6) {
                    return false;
                }
                if (board[fromRow + rowDiff / 2][fromCol].type != PieceType.EMPTY) {
                    return false;
                }
            }
            return board[toRow][toCol].type == PieceType.EMPTY;
        }
    }


    boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // Check if the move is L-shaped
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }


    boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // Check if the move is diagonal
        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            return false;
        }

        // Check if the path is clear
        int rowStep = rowDiff == 0 ? 0 : rowDiff / Math.abs(rowDiff);
        int colStep = colDiff == 0 ? 0 : colDiff / Math.abs(colDiff);
        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;
        while (currentRow != toRow) {
            if (board[currentRow][currentCol].type != PieceType.EMPTY) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }


    boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // Check if the move is horizontal or vertical
        if (rowDiff != 0 && colDiff != 0) {
            return false;
        }

        // Check if the path is clear
        int rowStep = 0;
        int colStep = 0;
        if (rowDiff != 0) {
            rowStep = rowDiff / Math.abs(rowDiff);
        }
        if (colDiff != 0) {
            colStep = colDiff / Math.abs(colDiff);
        }
        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;
        while (currentRow != toRow || currentCol != toCol) {
            if (board[currentRow][currentCol].type != PieceType.EMPTY) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }


    boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        return isValidBishopMove(fromRow, fromCol, toRow, toCol) || isValidRookMove(fromRow, fromCol, toRow, toCol);
    }

    boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // Check if the move is too far
        return rowDiff <= 1 && colDiff <= 1;
    }


    boolean willPutKingInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        // Save the piece that was in the "to" square
        Piece savedPiece = board[toRow][toCol];

        // Make the move on the board
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = new Piece(PieceType.EMPTY, true);

        // Check if the king is in check after the move
        boolean result = isKingInCheck();

        // Undo the move
        board[fromRow][fromCol] = board[toRow][toCol];
        board[toRow][toCol] = savedPiece;

        return result;
    }

    boolean isKingInCheck() {
        // Find the position of the king
        int kingRow = -1, kingCol = -1;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece.type == PieceType.KING && piece.isWhite == whiteOnTurn) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        // Check if any opposing pieces can attack the king
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece.type != PieceType.EMPTY && piece.isWhite != whiteOnTurn) {
                    if (isValidMove(row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board[fromRow][fromCol];
        if (piece.isWhite != whiteOnTurn) {
            return false;  // Can only move pieces of your own color
        }
        return switch (piece.type) {
            case PAWN -> isValidPawnMove(fromRow, fromCol, toRow, toCol, piece.isWhite);
            case KNIGHT -> isValidKnightMove(fromRow, fromCol, toRow, toCol);
            case BISHOP -> isValidBishopMove(fromRow, fromCol, toRow, toCol);
            case ROOK -> isValidRookMove(fromRow, fromCol, toRow, toCol);
            case QUEEN -> isValidQueenMove(fromRow, fromCol, toRow, toCol);
            case KING -> isValidKingMove(fromRow, fromCol, toRow, toCol);
            default -> false;
        };
    }

    List<Move> getAllValidMovesForColor(boolean color) {
        List<Move> validMoves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece.isWhite == color) {
                    validMoves.addAll(getLegalMoves(row, col));
                }
            }
        }
        return validMoves;
    }

    record ScoreAndMove(int score, Move move) {
            @Override
            public String toString() {
                return "ScoreAndMove{" +
                        "score=" + score +
                        ", move=" + move.toString() +
                        '}';
            }

    }

    ScoreAndMove minimax(int depth, boolean maximizingPlayer) {
        if (depth == 0 || isGameOver()) {
            return new ScoreAndMove(evaluate(), null);
        }

        List<Move> moves = getAllValidMovesForColor(maximizingPlayer);
        Move bestMove = null;
        int bestValue;
        if (maximizingPlayer) {
            bestValue = Integer.MIN_VALUE;
            for (Move move : moves) {
                makeMove(move);
                int value = minimax(depth - 1, false).score;
                undoMove(move);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            }
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Move move : moves) {
                makeMove(move);
                int value = minimax(depth - 1, true).score;
                undoMove(move);
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            }
        }
        return new ScoreAndMove(bestValue, bestMove);
    }

    boolean isGameOver() {
        if (isCheckmate()) {
            return true;
        }
        if (isStalemate()) {
            return true;
        }
        return isDraw();
    }

    boolean isCheckmate() {
        return isKingInCheck() && getAllValidMovesForColor(whiteOnTurn).isEmpty();
    }

    boolean isStalemate() {
        return !isKingInCheck() && getAllValidMovesForColor(whiteOnTurn).isEmpty();
    }

    boolean isDraw() {

        // Return true if there are only bishops, knights, or a bishop and knight on the board, which is not enough material to checkmate
        if (isDrawByInsufficientMaterial()) {
            return true;
        }

        // Return true if there have been 50 moves without a capture or pawn move
        if (isDrawBy50MoveRule()) {
            return true;
        }

        // Return true if the same position has occurred three times, or if a claim for a draw by repetition has been made and the opponent accepts
        return isDrawByRepetition();

        // Return false if none of the draw conditions are met
    }

    boolean isDrawByInsufficientMaterial() {
        int pieceCount = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col].type != PieceType.EMPTY) {
                    pieceCount++;
                }
            }
        }
        return pieceCount == 2;
    }

    public boolean isDrawBy50MoveRule() {
        return halfMoveClock >= 100;
    }


    public boolean isDrawByRepetition() {
        Set<String> positions = new HashSet<>();
        for (int i = moveHistory.size() - halfMoveClock; i < moveHistory.size(); i++) {
            String position = Arrays.deepToString(moveHistory.get(i).board);
            if (positions.contains(position)) {
                return true;
            }
            positions.add(position);
        }
        return false;
    }

    private int evaluate() {
        int score = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece == null) continue;
                int pieceValue = switch (piece.type) {
                    case PAWN -> 1;
                    case KNIGHT, BISHOP -> 3;
                    case ROOK -> 5;
                    case QUEEN -> 9;
                    case KING -> 900;
                    default -> 0;
                };
                if (piece.isWhite) {
                    score += pieceValue;
                } else {
                    score -= pieceValue;
                }
            }
        }
        return score;
    }


}