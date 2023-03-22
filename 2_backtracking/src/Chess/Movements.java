package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Movements {

    public static Point[] straightTop(Point piece_position, ChessBoard board_state) {
        return IntStream.range(0, piece_position.y)
                .boxed()
                .sorted(Collections.reverseOrder())
                .map((y) -> new Point(piece_position.x, y))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] straightRight(Point piece_position, ChessBoard board_state) {
        return IntStream.range(piece_position.x + 1, board_state.width)
                .mapToObj((x) -> new Point(x, piece_position.y))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] straightBottom(Point piece_position, ChessBoard board_state) {
        return IntStream.range(piece_position.y + 1, board_state.height)
                .mapToObj(y -> new Point(piece_position.x, y))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] straightLeft(Point piece_position, ChessBoard board_state) {
        return IntStream.range(0, piece_position.x)
                .boxed()
                .sorted(Collections.reverseOrder())
                .map((x) -> new Point(x, piece_position.y))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalTopRight(Point piece_position, ChessBoard board_state) {
        return IntStream
                .range(1, Math.min(board_state.width - piece_position.x,
                        piece_position.y + 1))
                .mapToObj(i -> new Point(piece_position.x + i, piece_position.y - i))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalTopLeft(Point piece_position, ChessBoard board_state) {
        return IntStream.range(1, Math.min(piece_position.x + 1, piece_position.y + 1))
                .mapToObj(i -> new Point(piece_position.x - i, piece_position.y - i))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalBottomRight(Point piece_position, ChessBoard board_state) {
        return IntStream
                .range(1, Math.min(board_state.width - piece_position.x,
                        board_state.height - piece_position.y))
                .mapToObj(i -> new Point(piece_position.x + i, piece_position.y + i))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalBottomLeft(Point piece_position, ChessBoard board_state) {
        return IntStream
                .range(1, Math.min(piece_position.x + 1,
                        board_state.height - piece_position.y))
                .mapToObj(i -> new Point(piece_position.x - i, piece_position.y + i))
                .takeWhile(point -> !board_state.isOccupied(point))
                .toArray(Point[]::new);

    }

    public static Point[] jumpPermutation(Point piece_position, ChessBoard board_state, int[] permutation1,
            int[] permutation2) {
        return generatePermutations(permutation1, permutation2)
                .map(move -> new Point(move[0] + piece_position.x, move[1] + piece_position.y))
                .filter(point -> board_state.sanityCheck(point) && !board_state.isOccupied(point))
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
    private static Stream<int[]> generatePermutations(int[] nums1, int[] nums2) {
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
