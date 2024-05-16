import java.util.*;

public class MiniMaxGame {
    static final char[] VALID_CHARS = {'C', 'S', 'E'};
    static final char EMPTY = '-';
    static final int SIZE = 3;
    static char[][] board = new char[SIZE][SIZE];
    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeBoard();
        printBoard();

        int currentPlayer = random.nextInt(2);
        System.out.println((currentPlayer == 0 ? "Computer" : "Player") + " starts the game.");

        while (true) {
            if (currentPlayer == 0) {
                int[] move = findBestMove();
                board[move[0]][move[1]] = selectBestCharForPosition(move[0], move[1]);
                System.out.println("Computer plays at " + move[0] + "," + move[1] + " with " + board[move[0]][move[1]]);
            } else {
                playerMove();
                System.out.println("Player has made their move.");
            }
            printBoard();
            if (checkWin()) {
                System.out.println("Player " + (currentPlayer == 0 ? "Computer" : "Human") + " wins!");
                break;
            }
            if (isBoardFull()) {
                System.out.println("It's a draw!");
                break;
            }
            currentPlayer = 1 - currentPlayer;
        }
    }

    static void initializeBoard() {
        for (char[] row : board)
            Arrays.fill(row, EMPTY);
        board[1][random.nextBoolean() ? 0 : 2] = 'S';
    }

    static void printBoard() {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static boolean checkWin() {
        return checkLines("CSE") || checkLines("ESC");
    }

    static boolean checkLines(String pattern) {
        char c1 = pattern.charAt(0), c2 = pattern.charAt(1), c3 = pattern.charAt(2);
        return (checkLine(c1, c2, c3) || checkLine(c3, c2, c1));
    }

    static boolean checkLine(char c1, char c2, char c3) {
        for (int i = 0; i < SIZE; i++) {
            if ((board[i][0] == c1 && board[i][1] == c2 && board[i][2] == c3) ||
                    (board[0][i] == c1 && board[1][i] == c2 && board[2][i] == c3) ||
                    (board[0][0] == c1 && board[1][1] == c2 && board[2][2] == c3) ||
                    (board[0][2] == c1 && board[1][1] == c2 && board[2][0] == c3)) {
                return true;
            }
        }
        return false;
    }

    static boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    static void playerMove() {
        int x, y;
        char playerChar;
        do {
            System.out.println("Enter row, column, and character (C, S, or E):");
            x = scanner.nextInt();
            y = scanner.nextInt();
            playerChar = scanner.next().toUpperCase().charAt(0);
        } while (!isValidMove(x, y, playerChar));
        board[x][y] = playerChar;
    }

    static boolean isValidMove(int x, int y, char playerChar) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == EMPTY && isValidChar(playerChar);
    }

    static boolean isValidChar(char c) {
        return c == 'C' || c == 'S' || c == 'E';
    }

    static int[] findBestMove() {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = selectBestCharForPosition(i, j);
                    int moveValue = minimax(0, false);
                    board[i][j] = EMPTY;
                    if (moveValue > bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    static int minimax(int depth, boolean isMax) {
        if (checkWin()) return isMax ? 10 : -10;
        if (isBoardFull()) return 0;

        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = isMax ? selectBestCharForPosition(i, j) : selectBestCharForMin(i, j);
                    int score = minimax(depth + 1, !isMax);
                    board[i][j] = EMPTY;
                    bestScore = isMax ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    static char selectBestCharForPosition(int row, int col) {
        int bestValue = Integer.MIN_VALUE;
        char bestChar = EMPTY;
        for (char c : VALID_CHARS) {
            board[row][col] = c;
            int score = evaluate();
            if (score > bestValue) {
                bestValue = score;
                bestChar = c;
            }
            board[row][col] = EMPTY;
        }
        return bestChar;
    }

    static char selectBestCharForMin(int row, int col) {
        int lowestValue = Integer.MAX_VALUE;
        char bestChar = 'E';  // Default to 'E' if no other character offers a better option.

        for (char c : VALID_CHARS) {
            board[row][col] = c;
            int score = evaluate();
            if (score < lowestValue) {
                lowestValue = score;
                bestChar = c;
            }
            board[row][col] = EMPTY;  // Reset the board position after checking
        }
        return bestChar;
    }

    static int evaluate() {
        int score = 0;
        String[] patterns = {"CSE", "ESC"};
        for (String pattern : patterns) {
            if (checkLines(pattern)) score += 10;  // Assign a high score if a winning pattern exists
        }
        return score;
    }
}

