package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Move.MajorAttackMove;

public class Bishop extends Piece{
	
	private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -7, 7, 9};
	
	public Bishop(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.BISHOP,piecePosition, pieceAlliance, true);
	}

	public Bishop(final Alliance pieceAlliance, 
				final int piecePosition,
				final boolean isFirstMove) {
		super(PieceType.BISHOP,piecePosition, pieceAlliance, isFirstMove);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final int candidateCoordinateOffset: CANDIDATE_MOVE_COORDINATES) {
			int candidateDestinationCoordinate = this.piecePosition;
			while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				if(isFirstColumnExclusion(candidateCoordinateOffset, candidateDestinationCoordinate) ||
					isEighthColumnExclusion(candidateCoordinateOffset, candidateDestinationCoordinate)){
					break;
				}
				candidateDestinationCoordinate += candidateCoordinateOffset;
				if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
					final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
					if(pieceAtDestination == null) {
						legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
					} else {
						final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
						if(this.pieceAlliance != pieceAlliance) {
							legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
						}
						break;
					}
				}
			}	
		}		
		return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public Bishop movePiece(final Move move) {
		return new Bishop(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	
	@Override
	public String toString() {
		return PieceType.BISHOP.toString();
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return (BoardUtils.FIRST_COLUMN[candidateOffset] && ((currentPosition == -9 || currentPosition == 7)));
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return (BoardUtils.EIGHTH_COLUMN[candidateOffset] && ((currentPosition == -7 || currentPosition == 9)));
	}

}
