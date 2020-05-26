/**
 * Author:    Mustafa Yumurtacı
 * Created:   26.05.2020
 **/
package sample;

import java.util.ArrayList;
import java.util.List;

class SmithWaterman {
    int Match;
    int Mismatch;
    int Gap;
    String VerticalSequence;
    String HorizontalSequence;
    int[][] Matrix;
    int HighestScore;
    //for traceback starting points
    ArrayList<Cell> HighestScoringCells;
    //List<Cell> is a one backtrace, List<List<Cell>> is all backtraces
    ArrayList<ArrayList<Cell>> BackTraces;
    ArrayList<AlignedSequencePair> AlignedSequencePairs;

    public SmithWaterman(String verticalSequence, String horizontalSequence) {
        VerticalSequence = verticalSequence;
        HorizontalSequence = horizontalSequence;
        Matrix = new int[verticalSequence.length() + 1][horizontalSequence.length() + 1];
        HighestScoringCells = new ArrayList<Cell>();
        BackTraces = new ArrayList<ArrayList<Cell>>();
        AlignedSequencePairs = new ArrayList<AlignedSequencePair>();

    }

    public void SetValues(int match, int mismatch, int gap) {
        this.Match = match;
        this.Mismatch = mismatch;
        this.Gap = gap;

    }

    public void FillMatrix() {
        // Traverse matrix and calculate all values
        int highestScore = 0;
        for (int i = 1; i < Matrix.length; i++) {
            for (int j = 1; j < Matrix[0].length; j++) {
                int topValue = Matrix[i - 1][j] + Gap;
                int leftValue = Matrix[i][j - 1] + Gap;
                int diagonalValue = Matrix[i - 1][j - 1] +
                        (VerticalSequence.charAt(i - 1) == HorizontalSequence.charAt(j - 1) ? Match : Mismatch);
                Matrix[i][j] = Math.max(Math.max(topValue, leftValue), Math.max(diagonalValue, 0));
                highestScore = Matrix[i][j] > highestScore ? Matrix[i][j] : highestScore;
            }
        }

        HighestScore = highestScore;
    }

    public void FindHighestScoringCells() {
        for (int i = 1; i < Matrix.length; i++) {
            for (int j = 1; j < Matrix[0].length; j++) {
                if (Matrix[i][j] == HighestScore) {
                    HighestScoringCells.add(new Cell(i, j));
                }
            }
        }
    }

    public void TraceBack() {


        for (var cell : HighestScoringCells
        ) {
            var backTrace = new ArrayList<Cell>();

            backTrace.add(cell);

            //continue until reaching cell with 0 as a value
            while (Matrix[backTrace.get(backTrace.size() - 1).RowIndex][backTrace.get(backTrace.size() - 1).ColIndex] != 0) {
                var lastBackTrace = backTrace.get(backTrace.size() - 1);
                int topValue = Matrix[lastBackTrace.RowIndex - 1][lastBackTrace.ColIndex] + Gap;
                int leftValue = Matrix[lastBackTrace.RowIndex][lastBackTrace.ColIndex - 1] + Gap;
                int diagonalValue = Matrix[lastBackTrace.RowIndex - 1][lastBackTrace.ColIndex - 1] +
                        (VerticalSequence.charAt(lastBackTrace.RowIndex - 1) ==
                                HorizontalSequence.charAt(lastBackTrace.ColIndex - 1)
                                ? Match
                                : Mismatch);

                int currentCellValue = Matrix[lastBackTrace.RowIndex][lastBackTrace.ColIndex];
                if (topValue == currentCellValue) {
                    backTrace.add(new Cell(lastBackTrace.RowIndex - 1, lastBackTrace.ColIndex));
                } else if (leftValue == currentCellValue) {
                    backTrace.add(new Cell(lastBackTrace.RowIndex, lastBackTrace.ColIndex - 1));
                } else if (diagonalValue == currentCellValue) {
                    backTrace.add(new Cell(lastBackTrace.RowIndex - 1, lastBackTrace.ColIndex - 1));
                } else {
                    System.out.println("An error occured during traceback process!");
                    return;
                }
            }

            BackTraces.add(backTrace);
        }
    }

    public void AlignSequences() {
        for (int i = 0; i < BackTraces.size(); i++) {
            String verticalAlignedSequence = "";
            String horizontalAlignedSequence = "";
            for (int j = 0; j < BackTraces.get(i).size() - 1; j++) {
                if (BackTraces.get(i).get(j).RowIndex - 1 == BackTraces.get(i).get(j + 1).RowIndex &&
                        BackTraces.get(i).get(j).ColIndex - 1 == BackTraces.get(i).get(j + 1).ColIndex) {
                    verticalAlignedSequence += VerticalSequence.charAt(BackTraces.get(i).get(j).RowIndex - 1);
                    horizontalAlignedSequence += HorizontalSequence.charAt(BackTraces.get(i).get(j).ColIndex - 1);
                } else if (BackTraces.get(i).get(j).RowIndex - 1 == BackTraces.get(i).get(j + 1).RowIndex &&
                        BackTraces.get(i).get(j).ColIndex == BackTraces.get(i).get(j + 1).ColIndex) {
                    verticalAlignedSequence += VerticalSequence.charAt(BackTraces.get(i).get(j).RowIndex - 1);
                    horizontalAlignedSequence += "-";
                } else {
                    verticalAlignedSequence += "-";
                    horizontalAlignedSequence += HorizontalSequence.charAt(BackTraces.get(i).get(j).ColIndex - 1);
                }
            }

            AlignedSequencePairs.add(
                    new AlignedSequencePair(new StringBuilder(verticalAlignedSequence).reverse().toString(),
                            new StringBuilder(horizontalAlignedSequence).reverse().toString()));

        }
    }

    public long Run() {
        long startTime = System.nanoTime();

        FillMatrix();
        FindHighestScoringCells();
        TraceBack();
        AlignSequences();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        return duration;
    }


}

class Cell {
    int RowIndex;
    int ColIndex;

    public Cell(int rowIndex, int colIndex) {
        this.RowIndex = rowIndex;
        this.ColIndex = colIndex;
    }
}

class AlignedSequencePair {
    String VerticalAlignedSequence;
    String HorizontalAlignedSequence;

    public AlignedSequencePair(String verticalAlignedSequence, String horizontalAlignedSequence) {
        this.VerticalAlignedSequence = verticalAlignedSequence;
        this.HorizontalAlignedSequence = horizontalAlignedSequence;
    }
}
