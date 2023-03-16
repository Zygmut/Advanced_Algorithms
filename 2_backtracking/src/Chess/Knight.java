package Chess;

import java.awt.Point;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Knight extends ChessPiece {

        public Knight() {
                super();
                this.symbol = 'K';
        }


        @Override
        public Point[] getMovements(ChessBoard board_state, Point piece_position) {
                Set<Point> currentPieces = board_state.getPieces().keySet();
                return generatePermutations(new int[] { -1, +1 }, new int[] { -2, 2 })
                                .map(move -> new Point(move[0] + piece_position.x, move[1] + piece_position.y))
                                .filter(board_state::sanityCheck)
                                .filter(point -> !currentPieces.contains(point))
                                .toArray(Point[]::new);
        }

        /**
         * Generic method to create permutations to create the knight's movements. Can
         * be useful to create a different type of knight, that could move in a square L
         * or something I don't know its 1am
         *
         * @param nums1
         * @param nums2
         * @return
         */
        private Stream<int[]> generatePermutations(int[] nums1, int[] nums2) {
                return IntStream.range(0, nums1.length)
                                .boxed()
                                .flatMap(i -> IntStream.range(0, nums2.length)
                                                .boxed()
                                                .filter(j -> nums1[i] != nums2[j])
                                                .map(j -> new int[] { nums1[i], nums2[j] }))
                                .flatMap(arr -> Stream.of(
                                                new int[] { arr[0], arr[1] },
                                                new int[] { arr[1], arr[0] }));
        }



}
