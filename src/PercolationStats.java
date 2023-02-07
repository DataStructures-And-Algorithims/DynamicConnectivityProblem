/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */


import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    int i = 0;
    Percolation percolation;
    int row, col;
    double[] probabilities;
    double probability;

    int trials;

    public PercolationStats(int n, int trials) {
        this.trials = trials;
        percolation = new Percolation(n);
        probabilities = new double[trials];
        while (i < trials) {
            row = StdRandom.uniformInt(1, n + 1);
            col = StdRandom.uniformInt(1, n + 1);
            if (!(percolation.percolates())) {
                percolation.open(row, col);
            }
            // Percolation has occurred
            if (percolation.percolates()) {
                double numberOfOpenSites = new Double(percolation.numberOfOpenSites());
                double gridSize = new Double(percolation.getGridSize());
                probability = numberOfOpenSites / gridSize;
                probabilities[i] = probability;
                // Close all open sites
                percolation.closeGrid();
                // A trial ends when percolation occurs
                ++i;
            }
        }
    }

    public double mean() {
        return StdStats.mean(probabilities);
    }

    public double stddev() {
        return StdStats.stddev(probabilities);
    }

    public double confidenceLo() {
        double T = new Double(trials);
        return mean() - ((1.96 * stddev()) / ((T * T) / T));
    }

    public double confidenceHi() {
        double T = new Double(trials);
        return mean() + ((1.96 * stddev()) / ((T * T) / T));
    }

    public void printResult() {
        System.out.println("mean" + " = " + mean());
        System.out.println("stddev" + " = " + stddev());
        System.out.println(
                "95% interval " + " = " + "[ " + confidenceHi() + " ]" + "[ " + confidenceLo()
                        + " ]");
    }

    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(200, 100);
        ps.mean();
        ps.stddev();
        ps.confidenceHi();
        ps.confidenceLo();
        ps.printResult();
    }
}
