package com.miracle.engine.recyclerview.asymmetricgrid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class GridHelper {

    public static final int MASK_TOP = 0x0001;
    public static final int MASK_BOTTOM = 0x0010;
    public static final int MASK_LEFT = 0x0100;
    public static final int MASK_RIGHT = 0x1000;

    public static class GridBundle {

        private SizeItem[][] grid;
        private int[] gridEdgesMasks;

        public static GridBundle calculate(SizeItem[] sizeItems){

            GridBundle gridBundle = new GridBundle();

            SizeItem[][] grid = calculateMaximallySquareGrid(sizeItems);
            if(grid==null) return null;
            gridBundle.grid = grid;

            int[] gridEdgesMasks = calculateGridEdgesMask(grid);
            if(gridEdgesMasks==null) return null;
            gridBundle.gridEdgesMasks = gridEdgesMasks;

            return gridBundle;
        }


        @NonNull
        public SizeItem[][] getGrid() {
            return grid;
        }

        @NonNull
        public int[] getGridEdgesMasks() {
            return gridEdgesMasks;
        }
    }

    @Nullable
    public static SizeItem[][] calculateMaximallySquareGrid(SizeItem[] sizeItems){

        if(sizeItems==null) return null;

        int numObjects = sizeItems.length;
        int numVariations = 1 << (numObjects - 1); // calculate number of variations

        float sumAspectRatio = 0;
        for (SizeItem sizeItem : sizeItems) {
            sumAspectRatio += sizeItem.getAspectRatio();
        }

        float desiredRadius = (float) Math.sqrt(sumAspectRatio);
        float desiredAspectRatio = 1f;

        float minDeviation = Float.MAX_VALUE;
        int[][] maximallyRPositions = null;

        for(int i=0; i<numVariations; i++) { // loop over all variations
            int[][] positions = new int[numObjects][2];
            int mask = 1;
            int row = 0;
            int cell = 0;
            float cellsDeviation = 0;
            float rowAspectRatio = 0;
            float totalAspectRatio = 0;
            for(int j=0; j<numObjects; j++) {
                positions[j][0] = row;
                positions[j][1] = cell++;
                rowAspectRatio+=sizeItems[j].getAspectRatio();
                if((i & mask) != 0 || j==numObjects-1) { // check if current bit is set
                    totalAspectRatio+=1/rowAspectRatio;
                    cellsDeviation+=Math.abs(desiredRadius-cell);
                    rowAspectRatio = 0;
                    row++;
                    cell=0;
                }
                mask <<= 1;
            }
            float aspectRatioDeviation = Math.abs(desiredAspectRatio-totalAspectRatio);
            float rowsDeviation = Math.abs(desiredRadius-row);
            float deviation = aspectRatioDeviation+rowsDeviation+cellsDeviation/row;

            if(deviation<minDeviation||maximallyRPositions==null){
                minDeviation = deviation;
                maximallyRPositions = positions;
            }
        }

        if(maximallyRPositions!=null) {

            int lastRowIndex = maximallyRPositions[maximallyRPositions.length - 1][0];
            SizeItem[][] positions = new SizeItem[lastRowIndex+1][];
            int row = 0;
            int rowStart = 0;

            for (int i=0; i<maximallyRPositions.length; i++){
                if(maximallyRPositions[i][0]>row){
                    positions[row++] = Arrays.copyOfRange(sizeItems, rowStart, i);
                    rowStart = i;
                }
            }
            positions[row] = Arrays.copyOfRange(sizeItems, rowStart, numObjects);

            return positions;
        }
        return null;
    }

    @Nullable
    public static int[] calculateGridEdgesMask(SizeItem[][] grid){

        if(grid==null) return null;

        int[] positionsMasks;
        int count = 0;
        for (SizeItem[] row:grid) { count+= row.length;}

        positionsMasks = new int[count];
        count = 0;
        for (int i = 0; i < grid.length; i++) {
            SizeItem[] row = grid[i];
            for (int j = 0; j < row.length; j++) {
                int mask = 0;
                if (i == 0) {
                    mask|=MASK_TOP;
                }
                if (i == grid.length - 1) {
                    mask|=MASK_BOTTOM;
                }
                if (j == 0) {
                    mask|=MASK_LEFT;
                }
                if (j == row.length - 1) {
                    mask|=MASK_RIGHT;
                }
                positionsMasks[count++] = mask;
            }
        }
        return positionsMasks;
    }

    public static class SizeItem {

        private final int width;
        private final int height;
        private final float aspectRatio;

        public SizeItem(int width, int height) {
            this.width = width;
            this.height = height;
            aspectRatio = (float)width/height;
        }

        public int getWidth(){
            return width;
        }

        public int getHeight(){
            return height;
        }

        public float getAspectRatio(){
            return aspectRatio;
        }
    }

}
