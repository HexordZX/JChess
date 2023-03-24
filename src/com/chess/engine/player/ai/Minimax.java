package com.chess.engine.player.ai;

import java.util.Collection;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

public class Minimax implements MoveStrategy{
	
	private final BoardEvaluator boardEvaluator;
	private final int searchDepth;
	
	public Minimax (final int searchDepth) {
		this.boardEvaluator = new StandardBoardEvaluator();
		this.searchDepth = searchDepth;
	}
	
	@Override
	public String toString() {
		return "Minimax";
	}
	
	@Override
	public Move execute (Board board) {
		
		final long startTime = System.currentTimeMillis();
		Move bestMove = null;
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
		
		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);

		final Collection<Move> legalMoves = board.currentPlayer().getLegalMoves();
		for(final Move move: legalMoves) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if(moveTransition.getMoveStatus().isDone()) {
				
				currentValue = board.currentPlayer().getAlliance().isWhite() ?
						min(moveTransition.getTransitionBoard(), this.searchDepth -1, alpha, beta) :
						max(moveTransition.getTransitionBoard(), this.searchDepth -1, alpha, beta);
				
				if(board.currentPlayer().getAlliance().isWhite() && 
						currentValue >= highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
					alpha = currentValue;
				} else if(board.currentPlayer().getAlliance().isBlack() && 
						currentValue <= lowestSeenValue) {
					lowestSeenValue = currentValue;
					bestMove = move;
					beta = currentValue;
				}
			}
		}
		
		final long executionTime = System.currentTimeMillis() - startTime;
		System.out.println(bestMove);
		return bestMove;
	}
	
	private boolean isEndGameScenario(final Board board) {
		return board.currentPlayer().isInCheckMate() ||
				board.currentPlayer().isInStaleMate();
	}
	
	public int min(final Board board, 
				   final int depth,
				   int alpha,
				   int beta) {
		if(depth == 0 || isEndGameScenario(board)) {
			return this.boardEvaluator.evaluate(board, depth);
		}
		int lowestSeenValue = Integer.MAX_VALUE;
		for(final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if(moveTransition.getMoveStatus().isDone()) {
				final int currentValue = max(moveTransition.getTransitionBoard(), depth -1, alpha, beta);
				if(currentValue <= lowestSeenValue) {
					lowestSeenValue = currentValue;
				}
				if (lowestSeenValue <= alpha) {
                    return lowestSeenValue;
                }
                if (lowestSeenValue < beta) {
                    beta = lowestSeenValue;
                }
			}
		}
		return lowestSeenValue;
	}
	
	

	public int max(final Board board, 
				   final int depth,
				   int alpha,
				   int beta) {
		if(depth == 0 || isEndGameScenario(board)) {
			return this.boardEvaluator.evaluate(board, depth);
		}
		int highestSeenValue = Integer.MIN_VALUE;
		for(final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if(moveTransition.getMoveStatus().isDone()) {
				final int currentValue = min(moveTransition.getTransitionBoard(), depth -1, alpha, beta);
				if(currentValue >= highestSeenValue) {
					highestSeenValue = currentValue;
				}
				if (highestSeenValue >= alpha) {
                    return highestSeenValue;
                }
                if (highestSeenValue > beta) {
                    beta = highestSeenValue;
                }
			}
		}
		return highestSeenValue;
	}
}
