package chess;

import java.awt.Point;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Movements {

    private Movements() {
    }

    public static Point[] straightTop(Point piecePosition, ChessBoard boardState) {
        return IntStream.range(0, piecePosition.y)
                .boxed()
                .sorted(Collections.reverseOrder())
                .map((y) -> new Point(piecePosition.x, y))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] straightRight(Point piecePosition, ChessBoard boardState) {
        return IntStream.range(piecePosition.x + 1, boardState.width)
                .mapToObj((x) -> new Point(x, piecePosition.y))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] straightBottom(Point piecePosition, ChessBoard boardState) {
        return IntStream.range(piecePosition.y + 1, boardState.height)
                .mapToObj(y -> new Point(piecePosition.x, y))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] straightLeft(Point piecePosition, ChessBoard boardState) {
        return IntStream.range(0, piecePosition.x)
                .boxed()
                .sorted(Collections.reverseOrder())
                .map((x) -> new Point(x, piecePosition.y))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalTopRight(Point piecePosition, ChessBoard boardState) {
        return IntStream
                .range(1, Math.min(boardState.width - piecePosition.x,
                        piecePosition.y + 1))
                .mapToObj(i -> new Point(piecePosition.x + i, piecePosition.y - i))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalTopLeft(Point piecePosition, ChessBoard boardState) {
        return IntStream.range(1, Math.min(piecePosition.x + 1, piecePosition.y + 1))
                .mapToObj(i -> new Point(piecePosition.x - i, piecePosition.y - i))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalBottomRight(Point piecePosition, ChessBoard boardState) {
        return IntStream
                .range(1, Math.min(boardState.width - piecePosition.x,
                        boardState.height - piecePosition.y))
                .mapToObj(i -> new Point(piecePosition.x + i, piecePosition.y + i))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);
    }

    public static Point[] diagonalBottomLeft(Point piecePosition, ChessBoard boardState) {
        return IntStream
                .range(1, Math.min(piecePosition.x + 1,
                        boardState.height - piecePosition.y))
                .mapToObj(i -> new Point(piecePosition.x - i, piecePosition.y + i))
                .takeWhile(point -> !boardState.isOccupied(point))
                .toArray(Point[]::new);

    }

    public static Point[] jumpPermutation(Point piecePosition, ChessBoard boardState, int[] permutation1,
            int[] permutation2) {
        return generatePermutations(permutation1, permutation2)
                .map(move -> new Point(move[0] + piecePosition.x, move[1] + piecePosition.y))
                .filter(point -> boardState.sanityCheck(point) && !boardState.isOccupied(point))
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
