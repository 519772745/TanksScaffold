package Tools;

/**
 * MathTools class contains complex mathematical calculations used in the code.
 */
public class MathTools {

    /**
     * Calculates the distance between two points.
     *
     * @param x1 The x-coordinate of the first point
     * @param y1 The y-coordinate of the first point
     * @param x2 The x-coordinate of the second point
     * @param y2 The y-coordinate of the second point
     * @return The distance between the two points
     */
    public static float getDistance(float x1, float y1, float x2, float y2) {
        double distance_square = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        return (float) Math.sqrt(distance_square);
    }

    /**
     * Computes the value of a linear equation y = wx + b.
     *
     * @param x The input value
     * @param w The coefficient of x
     * @param b The constant term
     * @return The value of y for the given x
     */
    public static float Linear(float x, float w, float b) {
        return w * x + b;
    }

    /**
     * Computes the Simple Moving Average (SMA) of a data array.
     * Uses 0 to fill in, ensuring the length of the returned array remains unchanged.
     *
     * @param data       The data array to compute the moving average
     * @param windowSize The size of the moving average window
     * @return An array containing the moving averages
     */
    public static float[] calculateSMA(float[] data, int windowSize) {
        float[] movingAverages = new float[data.length];

        // Calculate the moving average
        for (int i = 0; i < data.length; i++) {
            float sum = 0.0f;
            int count = 0;
            for (int j = Math.max(0, i - windowSize + 1); j <= i; j++) {
                if (j < 0) {
                    sum += data[0]; // Fill with the first data point
                } else {
                    sum += data[j];
                }
                count++;
            }
            movingAverages[i] = sum / count;
        }

        return movingAverages;
    }
}
