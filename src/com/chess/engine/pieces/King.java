package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Move.MajorAttackMove;
import com.google.common.collect.ImmutableList;

public class King extends Piece {

	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };
	
	private final boolean isCastled;
	private final boolean kingSideCastleCapable;
	private final boolean queenSideCastleCapable;

	public King(final Alliance pieceAlliance, 
				final int piecePosition,
				final boolean kingSideCastleCapable,
				final boolean queenSideCastleCapable) {
		super(PieceType.KING,piecePosition, pieceAlliance, true);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}
	
	public King(final Alliance pieceAlliance, 
				final int piecePosition,
				final boolean isFirstMove,
				final boolean isCastled,
				final boolean kingSideCastleCapable,
				final boolean queenSideCastleCapable) {
		super(PieceType.KING,piecePosition, pieceAlliance, isFirstMove);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}
	
	public boolean isCastled() {
		return this.isCastled;
	}
	
	public boolean isKingSideCastleCapable(){
		return this.kingSideCastleCapable;
	}
	
	public boolean isQueenSideCastleCapable(){
		return this.queenSideCastleCapable;
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int currenCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			final int candidateDestinationCoordinate = this.piecePosition + currenCandidateOffset;
			if(isFirstColumnExclusion(this.piecePosition, currenCandidateOffset) || 
					isEighthColumnExclusion(this.piecePosition, currenCandidateOffset)) {
				continue;
			}
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {			
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				if (!candidateDestinationTile.isTileOccupied()) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
				} else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if (this.pieceAlliance != pieceAlliance) {
						legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
					}
				}
			}
		}
		return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public King movePiece(final Move move) {
		return new King(move.getMovedPiece().getPieceAlliance(), 
						move.getDestinationCoordinate(), 
						false, 
						move.isCastlingMove(),
						false,
						false);
	}
	
	public String toString() {
		return PieceType.KING.toString();
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
	}
}
