/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import java.util.Scanner;

public class Percolation {
    static Scanner scanChoice = new Scanner(System.in);
    static Scanner rowChoice = new Scanner(System.in);
    static Scanner colChoice = new Scanner(System.in);

    static Scanner end = new Scanner(System.in);

    static Scanner functionChoice = new Scanner(System.in);


    // Total number of nodes possible(Length n will hava n*n grid size )
    private int gridSize;
    private int gridLength = 0;
    private int[] grid;

    // Size of a connected component
    private int[] szOfConnectedComponent;
    // This array stores the root open sites
    // A root open site is an open site that has no adjacent sites that are open
    private int[] rootOpenSite;

    // This is the virtual root array for the top row
    private int[] virtualRoot;

    // Size of the tree that the current site belongs to
    private int treeSizeCurrent;

    // Size of the largest tree that has a top row site as root
    private int currentLargest;

    /* Initialize all sites to be closed
   A closed site is where grid[n] = n
    */
    public Percolation(int n) {
        gridLength = n;
        this.gridSize = n * n;
        grid = new int[gridSize + 1];
        szOfConnectedComponent = new int[gridSize + 1];
        rootOpenSite = new int[gridSize + 1];
        currentLargest = 0;
        virtualRoot = new int[2];
        virtualRoot[0] = 125;
        virtualRoot[1] = -125;
        // Set percolation.grid
        grid[0] = 0;
        for (int i = 1; i < grid.length; i++) {
            grid[i] = i;
        }
        // Set rootOpenSite values to 0 indication there are no open root sites
        rootOpenSite[0] = 0;
        for (int i = 1; i < rootOpenSite.length; i++) {
            rootOpenSite[i] = 0;
        }

        // Set szOfConnectedComponents
        szOfConnectedComponent[0] = 0;
        for (int i = 1; i < szOfConnectedComponent.length; i++) {
            szOfConnectedComponent[i] = 0;
        }
    }

    public int getGridSize() {
        return this.gridSize;
    }

    // An open site is created by combining at least 2 points
    // In order to ensure this datatype runs at O(N + M log *N) we will use weighted union with path compression
    // Given row and column position id should be calculated
    public void open(int row, int col) {
        // Get root of site
        int root = root(site(row, col));
        // Used to differentiate between closed site and root open site
        // If grid[i] == i and rootOpenSite[i] == 1 this is a root open site
        // If grid[i] == i and rootOpenSite[i] == 0 this is a closed site
        // if grid[i] != i and rootOpenSite[i] == 0 this is an adjacent open site

        // Closed site
        if (grid[root] == site(row, col) && rootOpenSite[site(row, col)] == 0) {
            // Set the size of this site as 1
            szOfConnectedComponent[site(row, col)] = 1;
            // Ensure row and col values are bounded
            int rAbove = ((row - 1) <= 1) ? 1 : row - 1;
            int rBelow = ((row + 1) >= gridLength) ? gridLength : row + 1;
            int cLeft = ((col - 1) <= 1) ? 1 : col - 1;
            int cRight = ((col + 1) >= gridLength) ? gridLength :
                    col + 1;

            // Check if there are any neighbours that are open
            if (isOpen(rAbove, col) || isOpen(rBelow, col) || isOpen(row, cLeft) || isOpen(
                    row, cRight)) {
                // Used here to prevent double access since only a closed site with neighbours will access here
                ++szOfConnectedComponent[0];
                // If Above open
                if (isOpen(rAbove, col)) {
                    // Use weightedUnion to open
                    weightedUnion(site(row, col), site(rAbove, col));
                }
                // Below open
                if (isOpen(rBelow, col)) {
                    weightedUnion(site(row, col), site(rBelow, col));
                }
                // Left open
                if (isOpen(row, cLeft)) {
                    weightedUnion(site(row, col), site(row, cLeft));
                }
                // Right open
                if (isOpen(row, cRight)) {
                    weightedUnion(site(row, col), site(row, cRight));
                }
            }
            else
            // Open this site as a root open site
            {
                rootOpenSite[site(row, col)] = 1;
                szOfConnectedComponent[site(row, col)] = 1;
                ++szOfConnectedComponent[0];
            }
        }
        // Set the virtualRoot for the opened site if it qualifies
        virtualRoot(site(row, col));
    }

    public boolean isOpen(int row, int col) {
        // An open site has grid[id] != id || rootOpenSite[id] == 1(For root open sites)
        return (grid[site(row, col)] != site(row, col)
                || rootOpenSite[site(row, col)] == 1);
    }

    // Check is a site is already opened, if opened it is full
    public boolean isFull(int row, int col) {
        if (isOpen(row, col)) {
            return true;
        }
        return false;
    }

    public int numberOfOpenSites() {
        return szOfConnectedComponent[0];
    }

    // All the elements of a connected path have the same root value
    public boolean percolates() {
        return virtualRoot[0] == virtualRoot[1];
    }

    // Get root of a particular element with path compression
    private int root(int i) {
        while (i != grid[i]) {
            grid[i] = grid[grid[i]];
            i = grid[i];
        }
        return i;
    }

    // Get site given row and column
    public int site(int row, int column) {
        return ((gridLength * row) - (gridLength - column));
    }

    public void weightedUnion(int siteOne, int siteTwo) {
        int rootSiteOne = root(siteOne);
        int rootSiteTwo = root(siteTwo);

        if (rootSiteOne == rootSiteTwo) return;

        if (szOfConnectedComponent[rootSiteOne] <= szOfConnectedComponent[rootSiteTwo]) {
            grid[rootSiteOne] = rootSiteTwo;
            szOfConnectedComponent[rootSiteTwo] += szOfConnectedComponent[rootSiteOne];
            // Set to zero since only root sites can hold info on size of component
            szOfConnectedComponent[rootSiteOne] = 0;
        }
        else {
            grid[rootSiteTwo] = rootSiteOne;
            szOfConnectedComponent[rootSiteOne] += szOfConnectedComponent[rootSiteTwo];
            // Set to zero since only root sites can hold info on size of component
            szOfConnectedComponent[rootSiteTwo] = 0;
        }
    }

    // Set virtualRoot
    public void virtualRoot(int site) {
        // There are different identities for topRow sites
        // A topRow site that is also a root
        if (root(site) <= gridLength) {
            for (int i = 1; i <= gridLength; i++) {
                treeSizeCurrent = szOfConnectedComponent[root(site)];
                int treeSizeNext = szOfConnectedComponent[root(i)];
                // If the root(site) == i it is obvious that the size of the tree is the same
                if (root(site) == i) {
                    treeSizeNext = treeSizeCurrent;
                }
                // The topRow site that belongs to the largest tree should have priority
                if ((treeSizeCurrent >= treeSizeNext) && (treeSizeCurrent > currentLargest)) {
                    virtualRoot[0] = root(site);
                    currentLargest = szOfConnectedComponent[root(site)];
                }
                // If the current site isn't the largest then the nextSite has to be the largest to alter virtualRoot  otherwise the value of currentLargest isn't accessed
                else if (treeSizeNext > currentLargest) {
                    virtualRoot[0] = root(i);
                    currentLargest = szOfConnectedComponent[root(i)];
                }
            }
        }
        // A topRow site that belongs to a connected component, and it is not the root
        else if (site <= gridLength && site != root(site)) {
            if (grid[site] == root(site)) {
                // Only alter if it's the largest tree
                if (szOfConnectedComponent[root(site)] > currentLargest) {
                    virtualRoot[0] = root(site);
                }
            }
        }
        // Any other site can alter the bottom row virtual site since any open site can be the one to lead to percolation
        if (site > gridLength) {
            // Only alter if it's larger or equal to than the current Largest and the size of the tree is greater than the gridLength [Minimum requirement for a tree that percolates]
            if (szOfConnectedComponent[root(site)] >= currentLargest && (
                    szOfConnectedComponent[root(site)] >= gridLength)) {
                virtualRoot[1] = root(site);
            }
        }
    }

    public void closeGrid() {
        // Set percolation.grid
        grid[0] = 0;
        for (int i = 1; i < grid.length; i++) {
            grid[i] = i;
        }
        // Set rootOpenSite values to 0 indication there are no open root sites
        rootOpenSite[0] = 0;
        for (int i = 1; i < rootOpenSite.length; i++) {
            rootOpenSite[i] = 0;
        }

        // Set szOfConnectedComponents
        szOfConnectedComponent[0] = 0;
        for (int i = 1; i < szOfConnectedComponent.length; i++) {
            szOfConnectedComponent[i] = 0;
        }
        // Set virtualRoot
        virtualRoot[0] = 125;
        virtualRoot[1] = -125;

    }

    public void printGridAtPerc() {
        System.out.println("Percolation.grid array");
        System.out.print("[");
        for (int i = 0; i < grid.length; i++) {
            System.out.print(grid[i] + ",");
        }
        System.out.println("]");
        System.out.println("virtualRoot" + " percolation: " + percolates());
        System.out.print("[");
        for (int i = 0; i < virtualRoot.length; i++) {
            System.out.print(virtualRoot[i] + ",");
        }
        System.out.println("]");
        System.out.println("Number of open sites: " + numberOfOpenSites());

    }

    public void printArrays() {
        System.out.println("Percolation.grid array");
        System.out.print("[");
        for (int i = 0; i < grid.length; i++) {
            System.out.print(grid[i] + ",");
        }
        System.out.println("]");

        System.out.println("szOfConnectedComponent array");
        System.out.print("[");
        for (int i = 0; i < szOfConnectedComponent.length; i++) {
            System.out.print(szOfConnectedComponent[i] + ",");
        }
        System.out.println("]");

        System.out.println("rootOpenSite array");
        System.out.print("[");
        for (int i = 0; i < rootOpenSite.length; i++) {
            System.out.print(rootOpenSite[i] + ",");
        }
        System.out.println("]");

        System.out.println("virtualRoot" + " percolation: " + percolates());
        System.out.print("[");
        for (int i = 0; i < virtualRoot.length; i++) {
            System.out.print(virtualRoot[i] + ",");
        }
        System.out.println("]");
        System.out.println("Number of open sites: " + numberOfOpenSites());
    }

    public static void main(String[] args) {
        System.out.println("Enter size of grid");
        int gridLength = scanChoice.nextInt();
        Percolation percolation = new Percolation(gridLength);
        int exit = 0;
        while (exit == 0) {
            System.out.println("Enter position to be opened");
            System.out.print("Enter row -> ");
            int r = rowChoice.nextInt();
            System.out.print("Enter column -> ");
            int c = colChoice.nextInt();
            percolation.open(r, c);
            percolation.printArrays();
            System.out.print("If you wish to finish enter 1 now otherwise enter 0: ");
            exit = end.nextInt();
            System.out.println(".........................................................");
        }
        rowChoice.close();
        colChoice.close();
        scanChoice.close();
        end.close();
        functionChoice.close();
    }
}




