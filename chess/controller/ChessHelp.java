package chess.controller;

import chess.model.board.BoardLocation;
import chess.model.board.ChessBoard;
import chess.model.pieces.*;

import java.util.HashMap;

import static chess.controller.GameController.containerForTheGame;

/**
 * Created by Stephen on 5/14/2014.
 */
public class ChessHelp
{
//    private static HashMap<String, Method> methodMap = new HashMap<String, Method>(){
//        {
//            try {
//                put("King", this.getClass().getDeclaredMethod("isValidKingMove"));
//                put("Queen", this.getClass().getMethod("isValidQueenMove"));
//                put("Bishop", this.getClass().getMethod("isValidBishopMove"));
//                put("Knight", this.getClass().getMethod("isValidKnightMove"));
//                put("Rook", this.getClass().getMethod("isValidRookMove"));
//                put("Pawn", this.getClass().getMethod("isValidPawnMove"));
//            } catch (NoSuchMethodException e) {
//                ErrorLogger.logError(e.getLocalizedMessage()+"\nThe methodMap in ChessHelp needs HELP!");
//            }
//        }
//    };

    public static ChessPiece getNewPiece( String rep, final char color )
    {
        return new HashMap<String, ChessPiece>()
        {
            {
                put("King", new King(color == 'l'));
                put("Queen", new Queen(color == 'l'));
                put("Bishop", new Bishop(color == 'l'));
                put("Knight", new Knight(color == 'l'));
                put("Rook", new Rook(color == 'l'));
                put("Pawn", new Pawn(color == 'l'));
            }
        }.get(rep);
    }

    public static String getPieceImageDir( ChessPiece cp )
    {
        String root = "C:\\Users\\Stephen\\Documents\\Neumont Work\\Quarter 3\\PRO180 - Java Lab\\Files\\GuiImages\\";

        if ( cp == null ) return root + "";
        return new HashMap<String, String>()
        {
            {
                put("n", root + "Knight\\nb.png");
                put("N", root + "Knight\\nw.png");
                put("q", root + "Queen\\qb.png");
                put("Q", root + "Queen\\qw.png");
                put("k", root + "King\\kb.png");
                put("K", root + "King\\kw.png");
                put("b", root + "Bishop\\bb.png");
                put("B", root + "Bishop\\bw.png");
                put("r", root + "Rook\\rb.png");
                put("R", root + "Rook\\rw.png");
                put("p", root + "Pawn\\pb.png");
                put("P", root + "Pawn\\pw.png");
            }
        }.get(cp.toString());

    }

    public static boolean isValidMove( BoardLocation start, BoardLocation destination )
    {

        if ( start.getPresentPiece() != null )
        {

            ChessPiece pieceToMove = start.getPresentPiece(); // FIXED : That's going to throw a NullPointer
//
//            try {
//                return (Boolean) methodMap.get(pieceToMove.getClass().getCanonicalName())
//                        .invoke(start, destination);
//            } catch (IllegalAccessException e) {
//                ErrorLogger.logError(e.getLocalizedMessage()+"\nCouldn't access the boolean isValid...Move()");
//            } catch (InvocationTargetException e) {
//                ErrorLogger.logError(e.getLocalizedMessage()+"\nReflection invocation failed ");
//            }

            if ( pieceToMove.isWhite() != GameController.isWhiteTurn() )
            {
                System.err.println("It's not " + ( !isWhiteTurn() ? "White's" : "Black's" ) + " turn to play.");
                return false;
            }

            // Knights don't worry about paths, for they jump
            if ( pathIsClear(start, destination) || pieceToMove.toString().equalsIgnoreCase("n") )
                return pieceToMove.isValidMove(start, destination);
        }

        return false;

    }

    public static boolean testMoveForCheck( BoardLocation start, BoardLocation destination )
    {
        assert start.getPresentPiece() != null;
        ChessPiece pieceToMove = start.getPresentPiece();

        if ( pathIsClear(start, destination, containerForTheGame) || pieceToMove.toString().equalsIgnoreCase("n") )
            return pieceToMove.isValidMove(start, destination);

        return false;
    }

    private static boolean isWhiteTurn()
    {
        return GameController.isWhiteTurn();
    }

    /**
     * Calculates the total distance that needs to be moved, pulled from BoardLocation
     * and then gets the number of steps that should be necessary by dividing
     * the calculated distance
     * i.e.: 5 right, 5 up for a slope of 1/1, stepped through 5 times
     *
     * @param start       BoardLocation that should have the moving piece
     * @param destination BoardLocation of the destination
     * @return whether or not the path is clear based on the math
     */
    public static boolean pathIsClear( BoardLocation start, BoardLocation destination )
    {
        ChessPiece startPiece = start.getPresentPiece();

        int startX = start.getX();
        int startY = start.getY();

        int destX = destination.getX();
        int destY = destination.getY();

        int dX, dY; // Deltas of movement

        // if its a vertical move
        if ( startX == destX )
        {
            dX = 0;
        }
        else
        { // Diagonal typed incrementer setter
            dX = ( startX - destX ) / Math.abs(startX - destX);
        }

        // if it's a horizontal
        if ( startY == destY )
        {
            dY = 0;
        }
        else
        { // Diagonal typed incrementer setter
            dY = ( startY - destY ) / Math.abs(startY - destY);
        }

//        modeOfTravel = (((dX / dY) == 1) ? ModeOfTravel.DIAGONAL : ModeOfTravel.STRAIGHT);

        if ( ( startPiece.toString().equalsIgnoreCase("q") ||
                startPiece.toString().equalsIgnoreCase("b") ||
                startPiece.toString().equalsIgnoreCase("p") ||
                startPiece.toString().equalsIgnoreCase("k") )
                && ( ( Math.abs(startX - destX) == 1 ) && ( Math.abs(startY - destY) == 1 ) && !startPiece.colorMatches(destination.getPresentPiece()) ) )
            return true; // They're moving one square. Who cares what is about to go down

        if ( ( startPiece.toString().equalsIgnoreCase("q") ||
                startPiece.toString().equalsIgnoreCase("r") ||
                startPiece.toString().equalsIgnoreCase("k") )
                && ( ( Math.abs(startX - destX) == 1 ) ^ ( Math.abs(startY - destY) == 1 ) && !startPiece.colorMatches(destination.getPresentPiece()) ) )
            return true;

        // Not equal because we can increment up or down, depending on the piece and side of the board

        // Increment to check along the algebraically determined path

        startX -= dX;// Positives (blacks) move "down"/"-" negatives (whites) move "up"/"-- or +"
        startY -= dY;// ^^^^

        BoardLocation nextSquareOnPath = new BoardLocation(startX, startY);
        nextSquareOnPath.placePiece(startPiece);
//            System.out.println(startPiece.fancyName());
//            System.out.println(nextSquareOnPath);

        BoardLocation currentSquare = containerForTheGame.getFunctionalBoard()[startY - 1][startX - 1];

        if ( currentSquare != null )
        {
            // If there isn't a piece there at the start
            return currentSquare.getPresentPiece() == null;
        }
        else
        {
            currentSquare = nextSquareOnPath;

            boolean isClear = pathIsClear(currentSquare, destination);

            currentSquare.remove();

            return isClear;
        }


//        System.out.printf("Move from %s to %s for %s was invalid\n", start.getName(), destination.getName(), start.getPresentPiece().fancyName());
//        return false;
    }

    public static boolean pathIsClear( BoardLocation start, BoardLocation destination, ChessBoard toOperate )
    {
        ChessPiece startPiece = start.getPresentPiece();

        if ( startPiece == null )
        {
            return true;
        }

        int startX = start.getX();
        int startY = start.getY();

        int destX = destination.getX();
        int destY = destination.getY();

        int dX, dY; // Deltas of movement

        // if its a vertical move
        if ( startX == destX )
        {
            dX = 0;
        }
        else
        { // Diagonal typed incrementer setter
            dX = ( startX - destX ) / Math.abs(startX - destX);
        }

        // if it's a horizontal
        if ( startY == destY )
        {
            dY = 0;
        }
        else
        { // Diagonal typed incrementer setter
            dY = ( startY - destY ) / Math.abs(startY - destY);
        }

        if ( dX == 0 && dY == 0 ) return true;

//        modeOfTravel = (((dX / dY) == 1) ? ModeOfTravel.DIAGONAL : ModeOfTravel.STRAIGHT);

        if ( ( startPiece.toString().equalsIgnoreCase("q") ||
                startPiece.toString().equalsIgnoreCase("b") ||
                startPiece.toString().equalsIgnoreCase("p") ||
                startPiece.toString().equalsIgnoreCase("k") )
                && ( ( Math.abs(startX - destX) == 1 ) && ( Math.abs(startY - destY) == 1 ) && !startPiece.colorMatches(destination.getPresentPiece()) ) )
            return true; // They're moving one square. Who cares what is about to go down

        if ( ( startPiece.toString().equalsIgnoreCase("q") ||
                startPiece.toString().equalsIgnoreCase("r") ||
                startPiece.toString().equalsIgnoreCase("k") )
                && ( ( Math.abs(startX - destX) == 1 ) ^ ( Math.abs(startY - destY) == 1 ) && !startPiece.colorMatches(destination.getPresentPiece()) ) )
            return true;

        // Not equal because we can increment up or down, depending on the piece and side of the board

        // Increment to check along the algebraically determined path

        startX -= dX;// Positives (blacks) move "down"/"-" negatives (whites) move "up"/"-- or +"
        startY -= dY;// ^^^^

        BoardLocation nextSquareOnPath = new BoardLocation(startX, startY);
        nextSquareOnPath.placePiece(startPiece);
//            System.out.println(startPiece.fancyName());
//            System.out.println(nextSquareOnPath);

        // If a piece is present,
        // return false
        // recurse

        BoardLocation currentSquare = toOperate.getFunctionalBoard()[startY - 1][startX - 1];

        if ( currentSquare != null && currentSquare.getPresentPiece() != null )
        {
            // If piece is present
            return false;
        }

        currentSquare = nextSquareOnPath;
//            currentSquare.placePiece(startPiece);

        boolean isClear = pathIsClear(currentSquare, destination, toOperate);

        currentSquare.remove();

        return isClear;
    }

    public static void printPlayerTurn()
    {
        System.out.println(GameController.isWhiteTurn() ? "White's turn" : "Black's turn");
    }

    public static void callCheck()
    {
        ChessBoard.CheckFinder finder = containerForTheGame.getCheckFinder();

        boolean blackInCheck = finder.blackIsInCheck(),
                whiteInCheck = finder.whiteIsInCheck();

        if ( blackInCheck )
        {

            if ( finder.blackIsInCheckMate() )
            {
                System.out.println("Black King is in checkmate");

                System.exit(0);
            }
            else
                System.out.println("Black King is in check");

//            System.exit(0);
        }
        else
        {
            System.out.println("Black King is not in check");
        }

        if ( whiteInCheck )
        {

            if ( finder.whiteIsInCheckMate() )
            {
                System.out.println("White King is in checkmate");

                System.exit(0);
            }
            else
            {
                System.out.println("White King is in check");
            }

        }
        else
        {
            System.out.println("White King is not in check");
        }

//        System.out.println("The game is in stalemate: "+ finder.gameIsInStaleMate());
        if ( ( !blackInCheck && !whiteInCheck ) && finder.gameIsInStaleMate() )
        {
            System.out.println("The game has reached stalemate, on "
                    + ( isWhiteTurn() ? "White's" : "Black's" ) + " turn.");

            System.exit(0);
        }

    }

    public static void promotePawn()
    {

        BoardLocation[][] functionalBoard = containerForTheGame.getFunctionalBoard();

        int lastRow = isWhiteTurn() ? 7 : 0; // Ends of the board depending on the turn

        for ( BoardLocation boardLocation : functionalBoard[lastRow] )
        {

            if ( boardLocation.getPresentPiece() != null && boardLocation.getPresentPiece() instanceof Pawn )
            {
                // promote to Queen
                Queen promoted = new Queen(isWhiteTurn());

                boardLocation.placePiece(promoted);
                // Overwrite the piece that
            }

        }


    }

    private enum ModeOfTravel
    {
        DIAGONAL,
        STRAIGHT
    }
}
