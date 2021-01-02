/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Solution;

import csp.Backtracking;
import csp.Graph;
import csp.SET;
import csp.ST;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author DAROUYc
 */
public class SolveProblem {

    private ST<String, SET<String>> domainTable;
    private int[][] valGrille;
    char[][] contraintesHoriz;
    char[][] contraintesVert;
    Graph graph = new Graph();
    boolean simpleBacktracking=true;
    int dimGrille;
    int dimension;
    JTextField[][] grille;
    ST<String, String> config;
    ArrayList<String> choices;

    public SolveProblem(String level, JTextField[][] grille, int dimension) {
        // --- Initialize grille values and constraint      
        this.dimension = dimension;
        dimGrille = 2 * dimension - 1;
        this.grille = grille;
        System.out.println("dimension de grille "+ dimGrille);
        
        // initialize level
        if(level.equalsIgnoreCase("easy"))
        {
             this.grille = easy();
        }else if(level.equalsIgnoreCase("normal"))
        {
            this.grille = normal();
        }else{
            this.grille = tricky();
        }
         
        //separate global grille to 3 grilles
        this.valGrille = new int[dimension][dimension];
        this.contraintesHoriz = new char[dimension][dimension - 1];
        this.contraintesVert = new char[dimension - 1][dimension];
        
     
        newGrille();
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public JTextField[][] getGrille() {
        return grille;
    }
    
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setGrille(JTextField[][] grille) {
        this.grille = grille;
    }
    
    // correct the mistakes  made by player
    public boolean verifyConstraints(JTextField[][] grille, int dimension) {
        
    // --- get values and verify constraints
        for (int i = 0; i < dimGrille; i++) 
        {
            for (int j = 0; j < dimGrille; j++) 
            {
                if (!grille[i][j].getText().equals("")) 
                {    
                    // verify inputs values
                    if (i % 2 == 0 && j % 2 == 0) 
                    {
                        try {
                            
                            //verify domain
                            int val = Integer.parseInt(grille[i][j].getText());
                            if (val < 1 || val > dimension) 
                            {
                                JOptionPane.showMessageDialog(null, "The value " + val + " in cell [" + i + ", " + j + "] is not in domain !");

                                return false;
                            }
                            valGrille[i / 2][j / 2] = val;
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Cell is empty [" + i + "," + j + "] !");
                            ex.printStackTrace();
                            return false;
                        }
                    } 
                    // --- Horizontal constaints : < et > ---
                    else if (i % 2 == 0 && j % 2 != 0) 
                    {
                        char contrHoriz = grille[i][j].getText().charAt(0);
                        if (!(contrHoriz == '<' || contrHoriz == '>')) 
                        {
                            JOptionPane.showMessageDialog(null, "the character in [" + (i + 1) + ", " + (j + 1) + "] not correct (must be '<' or '>') !");
                            return false;
                        }
                        contraintesHoriz[i / 2][(j - 1) / 2] = contrHoriz;
                        
                    } 
                    // ---- Vertical constraints : ⋀ et ⋁ ---
                    else if (i % 2 != 0 && j % 2 == 0) 
                    {
                        char contrVert = grille[i][j].getText().charAt(0);
                        if (!(contrVert == '⋀' || contrVert == '⋁')) 
                        {
                            JOptionPane.showMessageDialog(null, "the character in [" + (i + 1) + ", " + (j + 1) + "] not correct (must be '^' ou 'v') !");
                            return false;
                        }
                        contraintesVert[(i - 1) / 2][j / 2] = contrVert;
                        System.out.println("contrVert" + contraintesVert[(i - 1) / 2][j / 2]);
                    }
                }
            }
        }

        // Verify constaint between lines and columns 
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int val = valGrille[i][j];
                if (val != 0) 
                {
                    //Compare by column
                    for (int row = 0; row < dimension; row++) {
                        if (row != i) 
                        {
                            if (val == valGrille[row][j]) // if it's same value
                            {
                                JOptionPane.showMessageDialog(null, "Cell [" + (i + 1) + ", " + (j + 1) + "] it's repeated in the same column (cell [" + (row + 1) + ", " + (j + 1) + "]) !");
                                return false;
                            }
                        }
                    }
                    // Compare by lines
                    for (int col = 0; col < dimension; col++) {
                        if (col != j) 
                        {
                            if (val == valGrille[i][col]) //if it's same value
                            {
                                JOptionPane.showMessageDialog(null, "Cell [" + (i + 1) + ", " + (j + 1) + "] it's repeated in the same line (cell [" + (i + 1) + ", " + (col + 1) + "]) !");
                                return false;
                            }
                        }
                    }
                    // Verify constaint between horizontal cell  > et < 
                    // Left side ;
                    if (j != 0) 
                    {
                        if (contraintesHoriz[i][j - 1] != ' ') // if cell not empty
                        {

                            if (valGrille[i][j - 1] != 0) // if left cell not empty
                            {
                                switch (contraintesHoriz[i][j - 1]) // '<' and '>'
                                {
                                    case '>':
                                        if (valGrille[i][j - 1] < val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + "  > value " + valGrille[i][j - 1] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '<':
                                        if (valGrille[i][j - 1] > val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + "  < value " + valGrille[i][j - 1] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    //Right side
                    if (j != dimension - 1) 
                    {
                        if (contraintesHoriz[i][j] != ' ') // if cell not empty
                        {

                            if (valGrille[i][j + 1] != 0) // if right cell not empty
                            {
                                switch (contraintesHoriz[i][j]) // '<' and '>'
                                {
                                    case '>':
                                        if (valGrille[i][j + 1] > val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value" + val + " < value " + valGrille[i][j + 1] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '<':
                                        if (valGrille[i][j + 1] < val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + " value " + valGrille[i][j + 1] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    // Verify constaint between vertical cell : ⋀ et ⋁ 
                    // Top
                    if (i != 0) 
                    {
                        if (contraintesVert[i - 1][j] != ' ') //if cell not empty
                        {

                            if (valGrille[i - 1][j] != 0) // if top cell not empty
                            {
                                switch (contraintesVert[i - 1][j]) //  '⋀' and '⋁'
                                {
                                    case '⋀':
                                        if (valGrille[i - 1][j] > val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + "  < value " + valGrille[i - 1][j] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '⋁':
                                        if (valGrille[i - 1][j] < val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + " > value " + valGrille[i - 1][j] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    // low
                    if (i != dimension - 1) 
                    {
                        if (contraintesVert[i][j] != ' ') //if cell not empty
                        {

                            if (valGrille[i + 1][j] != 0) // if low cell not empty
                            {
                                switch (contraintesVert[i][j]) // '⋀' and '⋁'
                                {
                                    case '⋀':
                                        if (valGrille[i + 1][j] < val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + "  > value " + valGrille[i + 1][j] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '⋁':
                                        if (valGrille[i + 1][j] > val) 
                                        {
                                            JOptionPane.showMessageDialog(null, "Value " + val + " > value " + valGrille[i + 1][j] + "\n (cell [" + (i + 1) + ", " + (j + 1) + "] and [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public ST<String, SET<String>> getDomain() {
        // --- Lines constraints ---
        for (int i = 0; i < dimension; i++) // Line
        {
            for (int j = 0; j < dimension - 1; j++) // Column
            {
                for (int k = j + 1; k < dimension; k++) {
                    String val1 = "x" + i + "" + j;
                    String val2 = "x" + i + "" + k;
                    // make relation between horizontal cell
                    graph.addEdge(val1, val2);
                }
            }
        }
        // --- Columns constraints ---
        for (int i = 0; i < dimension; i++) { // Column
            for (int j = 0; j < dimension; j++) { // Line
                for (int k = j + 1; k < dimension; k++) {
                    String val1 = "x" + j + "" + i;
                    String val2 = "x" + k + "" + i;
                    // make relation between vertical cell
                    graph.addEdge(val2, val1);
                }

                System.out.println("At " + i + "," + j);
                
                // check vertical constraint if is not empty plus column is not first
                if (i > 0 && (contraintesVert[i - 1][j] == '⋀' || contraintesVert[i - 1][j] == '⋁')) {
                    System.out.println("Found contraites vert1 at " + i + "," + j + " = " + contraintesVert[i - 1][j]);

                    boolean cond = contraintesVert[i - 1][j] != '⋀';
                    
                    String val1 = cond ? "s" + (i - 1) + "" + j : "s" + i + "" + j;
                    String val2 = cond ? "x" + i + "" + j : "x" + (i - 1) + "" + j;
                    
                    // register sup value with 'S' character in graph
                    graph.addEdge(val2, val1);
                    
                    //register inf value with 'i' character in graph
                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");

                    graph.addEdge(val1, val2);

                }
                  // check vertical constraint if is not empty plus column is not first
                if (i < dimension - 1 && (contraintesVert[i][j] == '⋀' || contraintesVert[i][j] == '⋁')) {
                    System.out.println("Found contraites vert2 at " + i + "," + j + " = " + contraintesVert[i][j]);

                    boolean cond = contraintesVert[i][j] != '⋀';
                    
                    // register sup value with 'S' character in graph
                    String val1 = cond ? "s" + i + "" + j : "s" + (i + 1) + "" + j;
                    String val2 = cond ? "x" + (i + 1) + "" + j : "x" + i + "" + j;

                    graph.addEdge(val2, val1);
                    
                    //register inf value with 'i' character in graph
                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");

                    graph.addEdge(val1, val2);
                }
                 // check horizantal constraint if is not empty plus line is not first
                if (j < dimension - 1 && (contraintesHoriz[i][j] == '<' || contraintesHoriz[i][j] == '>')) {
                    System.out.println("Found contraites at " + i + "," + j + " = " + contraintesHoriz[i][j]);

                    boolean cond = contraintesHoriz[i][j] == '<';
                     // register sup value with 'S' character in graph
                    String val1 = cond ? "s" + i + "" + (j + 1) : "s" + i + "" + j;
                    String val2 = cond ? "x" + i + "" + j : "x" + i + "" + ( j + 1 );

                    graph.addEdge(val2, val1);

                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");
                     //register inf value with 'i' character in graph
                    graph.addEdge(val1, val2);
                }
                
                 // check horizantal constraint if is not empty plus line is not first
                if (j > 0 && (contraintesHoriz[i][j - 1] == '<' || contraintesHoriz[i][j - 1] == '>')) {
                    System.out.println("Found horiz contraites at " + i + "," + j + " = " + contraintesHoriz[i][j - 1]);

                    boolean cond = contraintesHoriz[i][j - 1] == '<';
                     // register sup value with 'S' character in graph
                    String val1 = cond ? "s" + i + "" + j : "s" + i + "" + ( j - 1);
                    String val2 = cond ? "x" + i + "" + ( j - 1) : "x" + i + "" + j;

                    graph.addEdge(val2, val1);

                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");
                     //register inf value with 'i' character in graph
                    graph.addEdge(val1, val2);
                }
            }
        }

        // --- Domain ---
        domainTable = new ST<String, SET<String>>();
        Object[][] domains = new Object[dimension][dimension];
        // --- Initialization of domains--- 
        for (int i = 0; i < dimension; i++) // Colonne
        {
            for (int j = 0; j < dimension; j++) // Ligne
            {
                domains[i][j] = new SET<String>();
            }
        }
        // --- Add values to domain  ---
        for (int i = 0; i < dimension; i++) // Column
        {
            for (int j = 0; j < dimension; j++) // Line
            {
                if (valGrille[i][j] != 0) {
                    ((SET<String>) domains[i][j]).add(new String(String.valueOf(valGrille[i][j]))); // Domaine avec une seule valeur (case remplie)
                } else {
                    for (int k = 1; k <= dimension; k++) {
                        ((SET<String>) domains[i][j]).add("" + k);
                    }
                }
            }
        }
        // --- Add domain to table---
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                domainTable.put("x" + i + "" + j, ((SET<String>) domains[i][j]));
            }
        }
        // --- Display domain ---
        System.out.println("\nLa table des domaines est : ");
        Set<String> keys = (Set<String>) domainTable.getST().keySet();
        for (String key : keys) {
            System.out.println("Le domaine de " + key + " est: " + domainTable.getST().get(key));
        }
        
        // --- initialize configuration ---
        config = new ST<String, String>();
        for (int i = 0; i < dimension; i++) // Line 
        {
            for (int j = 0; j < dimension; j++) // Column
            {
                this.config.put("x" + i + "" + j, ""); // empty cell
            }
        }

        return domainTable;
    }

    public ST<String, String> solve(ArrayList<String> var) {
        choices=var;
        //use backtracking for getting solution
        Backtracking backtracking = new Backtracking();
        domainTable = getDomain();
        System.out.println("Constraints: ");
        System.out.println(graph);
        
        // get solution from backtracking
        ST<String, String> result = backtracking.backtracking(this.config, domainTable, graph,choices);

        return result;

    }

    public void newGrille() {
        for (int i = 0; i < dimGrille; i++) {
            for (int j = 0; j < dimGrille; j++) {
                //get grille of values
                if (!grille[i][j].getText().equals("")) {
                    if (i % 2 == 0 && j % 2 == 0) {
                        int val = Integer.parseInt(grille[i][j].getText());
                        valGrille[i / 2][j / 2] = val;
                    } //get grille of horizontal constraint 
                    else if (i % 2 == 0 && j % 2 != 0) {
                        char contrHoriz = grille[i][j].getText().charAt(0);
                        contraintesHoriz[i / 2][ (j - 1) /2] = contrHoriz;
                        System.out.println(" " + contrHoriz + " i " + i + " j " + j);
                    }//get grille of vertical constraint  
                    else if (i % 2 != 0 && j % 2 == 0) {
                        char contrVert = grille[i][j].getText().charAt(0);
                        contraintesVert[(i - 1) / 2][j / 2] = contrVert;
                        System.out.println(" " + contrVert + " i " + i + " j ");
                    }
                }
            }
        }
    }

    // get grille of easy level 
    public JTextField[][] easy()
    {
        JTextField[][] easyGrille = grille;
        
         switch (dimension) {
            case 4:
                easyGrille[0][1].setText(">");
                easyGrille[0][1].setEditable(false);;
                easyGrille[1][2].setText("⋁");
                easyGrille[1][2].setEditable(false);
                easyGrille[2][4].setText("1");
                easyGrille[2][4].setEditable(false);
                easyGrille[5][4].setText("⋁");
                easyGrille[5][4].setEditable(false);
                break;
            case 5:
                easyGrille[0][7].setText(">");
                easyGrille[0][7].setEditable(false);
                easyGrille[1][0].setText("⋀");
                easyGrille[1][0].setEditable(false);
                easyGrille[2][1].setText("<");
                easyGrille[2][1].setEditable(false);
                easyGrille[2][2].setText("3");
                easyGrille[2][2].setEditable(false);
                easyGrille[5][0].setText("⋁");
                easyGrille[5][0].setEditable(false);
                easyGrille[5][2].setText("⋁");
                easyGrille[5][2].setEditable(false);
                easyGrille[5][6].setText("⋁");
                easyGrille[5][6].setEditable(false);
                break;
            case 6:
                easyGrille[0][4].setText("1");
                easyGrille[0][4].setEditable(false);
                easyGrille[2][2].setText("2");
                easyGrille[2][2].setEditable(false);
                easyGrille[3][2].setText("⋀");
                easyGrille[3][2].setEditable(false);
                easyGrille[4][6].setText("3");
                easyGrille[4][6].setEditable(false);
                easyGrille[4][9].setText(">");
                easyGrille[4][9].setEditable(false);
                easyGrille[7][6].setText("⋁");
                easyGrille[7][6].setEditable(false);
                easyGrille[7][8].setText("⋀");
                easyGrille[7][8].setEditable(false);
                easyGrille[9][2].setText("⋁");
                easyGrille[9][2].setEditable(false);
                easyGrille[9][6].setText("⋁");
                easyGrille[9][6].setEditable(false);
                easyGrille[10][10].setText("6");
                easyGrille[10][10].setEditable(false);
               
                break;
            case 7:
                easyGrille[0][1].setText(">");
                easyGrille[0][1].setEditable(false);
                easyGrille[0][4].setText("6");
                easyGrille[0][4].setEditable(false);
                easyGrille[0][12].setText("3");
                easyGrille[0][12].setEditable(false);
                easyGrille[1][2].setText("⋁");
                easyGrille[1][2].setEditable(false);
                easyGrille[1][4].setText("⋁");
                easyGrille[1][4].setEditable(false);
                easyGrille[1][8].setText("⋁");
                easyGrille[1][8].setEditable(false);
                easyGrille[2][9].setText(">");
                easyGrille[2][9].setEditable(false);
                easyGrille[2][12].setText("6");
                easyGrille[2][12].setEditable(false);
                easyGrille[3][8].setText("⋀");
                easyGrille[3][8].setEditable(false);
                easyGrille[4][1].setText(">");
                easyGrille[4][1].setEditable(false);
                easyGrille[5][4].setText("⋁");
                easyGrille[5][4].setEditable(false);
                easyGrille[5][12].setText("⋁");
                easyGrille[5][12].setEditable(false);
                easyGrille[6][3].setText("<");
                easyGrille[6][3].setEditable(false);
                easyGrille[6][6].setText("1");
                easyGrille[6][6].setEditable(false);
                easyGrille[8][11].setText(">");
                easyGrille[8][11].setEditable(false);
                easyGrille[9][0].setText("⋀");
                easyGrille[9][0].setEditable(false);
                easyGrille[10][5].setText("<");
                easyGrille[10][5].setEditable(false);
                easyGrille[11][8].setText("⋁");
                easyGrille[11][8].setEditable(false);
               
                break;
                case 8:
                easyGrille[0][5].setText("<");
                easyGrille[0][5].setEditable(false);
                easyGrille[0][11].setText(">");
                easyGrille[0][11].setEditable(false);
                easyGrille[1][2].setText("⋁");
                easyGrille[1][2].setEditable(false);
                easyGrille[1][4].setText("⋁");
                easyGrille[1][4].setEditable(false);
                easyGrille[1][10].setText("⋁");
                easyGrille[1][10].setEditable(false);
                easyGrille[1][12].setText("⋁");
                easyGrille[1][12].setEditable(false);
                easyGrille[1][14].setText("⋁");
                easyGrille[1][14].setEditable(false);
                easyGrille[2][1].setText("<");
                easyGrille[2][1].setEditable(false);
                easyGrille[3][10].setText("⋁");
                easyGrille[3][10].setEditable(false);
                easyGrille[3][14].setText("⋁");
                easyGrille[3][14].setEditable(false);
                easyGrille[4][3].setText("<");
                easyGrille[4][3].setEditable(false);
                easyGrille[4][4].setText("6");
                easyGrille[4][4].setEditable(false);
                easyGrille[4][10].setText("4");
                easyGrille[4][10].setEditable(false);
                easyGrille[4][13].setText("<");
                easyGrille[4][13].setEditable(false);
                easyGrille[5][8].setText("⋀");
                easyGrille[5][8].setEditable(false);
                easyGrille[6][2].setText("1");
                easyGrille[6][2].setEditable(false);
                easyGrille[6][4].setText("3");
                easyGrille[6][4].setEditable(false);
                easyGrille[7][14].setText("⋀");
                easyGrille[7][14].setEditable(false);
                easyGrille[8][9].setText("<");
                easyGrille[8][9].setEditable(false);
                easyGrille[9][0].setText("⋀");
                easyGrille[9][0].setEditable(false);
                easyGrille[9][6].setText("⋀");
                easyGrille[9][6].setEditable(false);
                easyGrille[9][3].setText("<");
                easyGrille[9][3].setEditable(false);
                easyGrille[9][13].setText(">");
                easyGrille[9][13].setEditable(false);
                easyGrille[10][3].setText("<");
                easyGrille[10][3].setEditable(false);
                easyGrille[10][13].setText(">");
                easyGrille[10][13].setEditable(false);
                easyGrille[12][5].setText("<");
                easyGrille[12][5].setEditable(false);
                easyGrille[12][8].setText("4");
                easyGrille[12][8].setEditable(false);
                easyGrille[12][12].setText("6");
                easyGrille[12][12].setEditable(false);
                easyGrille[13][10].setText("⋀");
                easyGrille[13][10].setEditable(false);
                
                
                break;
         }
        return easyGrille;
    }
    
    // get grille of normal level 
    public JTextField[][] normal()
    {
       JTextField[][] normalGrille =grille;
        
        switch (dimension) {
            case 4:
                normalGrille[0][3].setText("<");
                normalGrille[0][3].setEditable(false);;
                normalGrille[0][5].setText("<");
                normalGrille[0][5].setEditable(false);
                normalGrille[3][0].setText("⋁");
                normalGrille[3][0].setEditable(false);
                normalGrille[4][1].setText(">");
                normalGrille[4][1].setEditable(false);
                normalGrille[6][1].setText(">");
                normalGrille[6][1].setEditable(false);
                normalGrille[6][5].setText(">");
                normalGrille[6][5].setEditable(false);
                break;
            case 5:
                normalGrille[1][2].setText("⋁");
                normalGrille[1][2].setEditable(false);
                normalGrille[2][3].setText(">");
                normalGrille[2][3].setEditable(false);
                normalGrille[2][5].setText("<");
                normalGrille[2][5].setEditable(false);
                normalGrille[3][8].setText("⋁");
                normalGrille[3][8].setEditable(false);
                normalGrille[4][5].setText(">");
                normalGrille[4][5].setEditable(false);
                normalGrille[5][2].setText("⋀");
                normalGrille[5][2].setEditable(false);
                normalGrille[5][4].setText("⋀");
                normalGrille[5][4].setEditable(false);
                normalGrille[6][5].setText("<");
                normalGrille[6][5].setEditable(false);
                normalGrille[7][4].setText("⋁");
                normalGrille[7][4].setEditable(false);
                normalGrille[8][0].setText("3");
                normalGrille[8][0].setEditable(false);
                break;
            case 6:
                normalGrille[0][3].setText("<");
                normalGrille[0][3].setEditable(false);
                normalGrille[0][6].setText("6");
                normalGrille[0][6].setEditable(false);
                normalGrille[1][0].setText("⋁");
                normalGrille[1][0].setEditable(false);
                normalGrille[1][4].setText("⋀");
                normalGrille[1][4].setEditable(false);
                normalGrille[3][6].setText("⋀");
                normalGrille[3][6].setEditable(false);
                normalGrille[4][5].setText(">");
                normalGrille[4][5].setEditable(false);
                normalGrille[4][7].setText("<");
                normalGrille[4][7].setEditable(false);
                normalGrille[5][6].setText("⋀");
                normalGrille[5][6].setEditable(false);
                normalGrille[9][4].setText("⋁");
                normalGrille[9][4].setEditable(false);
                normalGrille[9][8].setText("⋁");
                normalGrille[10][1].setEditable(false);
                normalGrille[10][4].setText("4");
                normalGrille[10][5].setEditable(false);
                normalGrille[10][9].setText(">");
                normalGrille[10][9].setEditable(false);
    
                break;
            case 7:
                normalGrille[0][0].setText("6");
                normalGrille[0][0].setEditable(false);
                normalGrille[0][1].setText("<");
                normalGrille[0][1].setEditable(false);
                normalGrille[0][4].setText("2");
                normalGrille[0][4].setEditable(false);
                normalGrille[1][6].setText("⋁");
                normalGrille[1][6].setEditable(false);
                normalGrille[1][8].setText("⋁");
                normalGrille[1][8].setEditable(false);
                normalGrille[3][0].setText("⋀");
                normalGrille[3][0].setEditable(false);
                normalGrille[3][2].setText("⋁");
                normalGrille[3][2].setEditable(false);
                normalGrille[3][12].setText("⋁");
                normalGrille[3][12].setEditable(false);
                normalGrille[4][1].setText("<");
                normalGrille[4][1].setEditable(false);
                normalGrille[5][0].setText("⋀");
                normalGrille[5][0].setEditable(false);
                normalGrille[5][4].setText("⋀");
                normalGrille[5][4].setEditable(false);
                normalGrille[6][9].setText("<");
                normalGrille[6][9].setEditable(false);
                normalGrille[6][12].setText("7");
                normalGrille[6][12].setEditable(false);
                normalGrille[7][4].setText("⋁");
                normalGrille[7][4].setEditable(false);
                normalGrille[8][2].setText("3");
                normalGrille[8][2].setEditable(false);
                normalGrille[8][12].setText("5");
                normalGrille[8][12].setEditable(false);
                normalGrille[9][0].setText("⋁");
                normalGrille[9][0].setEditable(false);
                normalGrille[9][8].setText("⋁");
                normalGrille[9][8].setEditable(false);
                normalGrille[10][4].setText("5");
                normalGrille[10][4].setEditable(false);
                normalGrille[11][4].setText("⋁");
                normalGrille[11][4].setEditable(false);
                normalGrille[11][12].setText("⋀");
                normalGrille[11][12].setEditable(false);
                
                break;
                case 8:
                normalGrille[0][8].setText("4");
                normalGrille[0][8].setEditable(false);
                normalGrille[0][11].setText("<");
                normalGrille[0][11].setEditable(false);
                normalGrille[1][0].setText("⋀");
                normalGrille[1][0].setEditable(false);
                normalGrille[1][6].setText("⋁");
                normalGrille[1][6].setEditable(false);
                normalGrille[2][1].setText("<");
                normalGrille[2][1].setEditable(false);
                normalGrille[2][13].setText("<");
                normalGrille[2][13].setEditable(false);
                normalGrille[3][2].setText("⋀");
                normalGrille[3][2].setEditable(false);
                normalGrille[3][6].setText("⋁");
                normalGrille[3][6].setEditable(false);
                normalGrille[4][2].setText("7");
                normalGrille[4][2].setEditable(false);
                normalGrille[4][5].setText("<");
                normalGrille[4][5].setEditable(false);
                normalGrille[4][10].setText("1");
                normalGrille[4][10].setEditable(false);
                normalGrille[4][12].setText("3");
                normalGrille[4][12].setEditable(false);
                normalGrille[5][2].setText("⋁");
                normalGrille[5][2].setEditable(false);
                normalGrille[5][8].setText("⋁");
                normalGrille[5][8].setEditable(false);
                normalGrille[6][1].setText("<");
                normalGrille[6][1].setEditable(false);
                normalGrille[6][3].setText(">");
                normalGrille[6][3].setEditable(false);
                normalGrille[6][12].setText("5");
                normalGrille[6][12].setEditable(false);
                normalGrille[10][6].setText("6");
                normalGrille[10][6].setEditable(false);
                normalGrille[11][2].setText("⋀");
                normalGrille[11][2].setEditable(false);
                normalGrille[11][6].setText("⋁");
                normalGrille[11][6].setEditable(false);
                normalGrille[12][8].setText("7");
                normalGrille[12][8].setEditable(false);
                normalGrille[12][10].setText("4");
                normalGrille[12][10].setEditable(false);
                normalGrille[12][11].setText(">");
                normalGrille[12][11].setEditable(false);
                normalGrille[13][8].setText("⋁");
                normalGrille[13][8].setEditable(false);
                normalGrille[13][10].setText("⋀");
                normalGrille[13][10].setEditable(false);
                normalGrille[14][1].setText(">");
                normalGrille[14][1].setEditable(false);
                normalGrille[14][7].setText("<");
                normalGrille[14][7].setEditable(false);
                normalGrille[14][11].setText(">");
                normalGrille[14][11].setEditable(false);
                normalGrille[14][12].setText("7");
                normalGrille[14][12].setEditable(false);
                normalGrille[14][14].setText("5");
                normalGrille[14][14].setEditable(false);
                
                break;
        }
       return normalGrille;
    }
   
    // get grille of tricky level 
     public JTextField[][] tricky()
    {
        JTextField[][] trickyGrille = grille;
         switch (dimension) {
            case 4:
                trickyGrille[0][3].setText("<");
                trickyGrille[0][3].setEditable(false);;
                trickyGrille[2][1].setText(">");
                trickyGrille[2][1].setEditable(false);
                trickyGrille[2][5].setText(">");
                trickyGrille[2][5].setEditable(false);
                trickyGrille[5][4].setText("⋁");
                trickyGrille[5][4].setEditable(false);
                trickyGrille[5][6].setText("⋁");
                trickyGrille[5][6].setEditable(false);
                trickyGrille[6][1].setText(">");
                trickyGrille[6][1].setEditable(false);
                break;
            case 5:
                trickyGrille[0][1].setText("<");
                trickyGrille[0][1].setEditable(false);
                trickyGrille[0][3].setText("<");
                trickyGrille[0][3].setEditable(false);
                trickyGrille[0][6].setText("4");
                trickyGrille[0][6].setEditable(false);
                trickyGrille[0][7].setText(">");
                trickyGrille[0][7].setEditable(false);
                trickyGrille[1][8].setText("⋁");
                trickyGrille[1][8].setEditable(false);
                trickyGrille[3][6].setText("⋀");
                trickyGrille[3][6].setEditable(false);
                trickyGrille[5][6].setText("⋀");
                trickyGrille[5][6].setEditable(false);
                trickyGrille[7][8].setText("⋁");
                trickyGrille[7][8].setEditable(false);
                trickyGrille[8][1].setText(">");
                trickyGrille[8][1].setEditable(false);
                trickyGrille[8][5].setText(">");
                trickyGrille[8][5].setEditable(false);
                break;
            case 6:
                trickyGrille[0][3].setText("<");
                trickyGrille[0][3].setEditable(false);
                trickyGrille[0][9].setText(">");
                trickyGrille[0][9].setEditable(false);
                trickyGrille[0][10].setText("4");
                trickyGrille[0][10].setEditable(false);
                trickyGrille[1][0].setText("⋁");
                trickyGrille[1][0].setEditable(false);
                trickyGrille[1][6].setText("⋀");
                trickyGrille[1][6].setEditable(false);
                trickyGrille[1][10].setText("⋀");
                trickyGrille[1][10].setEditable(false);
                trickyGrille[2][0].setText("3");
                trickyGrille[2][0].setEditable(false);
                trickyGrille[4][1].setText("<");
                trickyGrille[4][1].setEditable(false);
                trickyGrille[4][3].setText(">");
                trickyGrille[4][3].setEditable(false);
                trickyGrille[4][7].setText(">");
                trickyGrille[4][7].setEditable(false);
                trickyGrille[4][9].setText(">");
                trickyGrille[4][9].setEditable(false);
                trickyGrille[5][0].setText("⋀");
                trickyGrille[5][0].setEditable(false);
                trickyGrille[6][2].setText("3");
                trickyGrille[6][2].setEditable(false);
                trickyGrille[6][7].setText("<");
                trickyGrille[6][7].setEditable(false);
                trickyGrille[7][2].setText("⋀");
                trickyGrille[7][2].setEditable(false);
                trickyGrille[7][10].setText("⋁");
                trickyGrille[7][10].setEditable(false);
                trickyGrille[8][5].setText("<");
                trickyGrille[8][5].setEditable(false);
                trickyGrille[9][0].setText("⋀");
                trickyGrille[9][0].setEditable(false);
    
    
                break;
            case 7:
                trickyGrille[0][11].setText(">");
                trickyGrille[0][11].setEditable(false);
                trickyGrille[1][0].setText("⋁");
                trickyGrille[1][0].setEditable(false);
                trickyGrille[1][10].setText("⋀");
                trickyGrille[1][10].setEditable(false);
                trickyGrille[2][5].setText(">");
                trickyGrille[2][5].setEditable(false);
                trickyGrille[2][7].setText("<");
                trickyGrille[2][7].setEditable(false);
                trickyGrille[6][7].setText("<");
                trickyGrille[6][7].setEditable(false);
                trickyGrille[6][9].setText("<");
                trickyGrille[6][9].setEditable(false);
                trickyGrille[7][0].setText("⋁");
                trickyGrille[7][0].setEditable(false);
                trickyGrille[9][0].setText("⋁");
                trickyGrille[9][0].setEditable(false);
                trickyGrille[9][4].setText("⋀");
                trickyGrille[9][4].setEditable(false);
                trickyGrille[9][6].setText("⋁");
                trickyGrille[9][6].setEditable(false);
                trickyGrille[10][5].setText("<");
                trickyGrille[10][5].setEditable(false);
                trickyGrille[10][9].setText(">");
                trickyGrille[10][9].setEditable(false);
                trickyGrille[11][0].setText("⋁");
                trickyGrille[11][0].setEditable(false);
                trickyGrille[11][2].setText("⋁");
                trickyGrille[11][2].setEditable(false);
                trickyGrille[12][1].setText(">");
                trickyGrille[12][1].setEditable(false);
                trickyGrille[12][5].setText(">");
                trickyGrille[12][5].setEditable(false);
                
                break;
                case 8:
                trickyGrille[1][0].setText("⋀");
                trickyGrille[1][0].setEditable(false);
                trickyGrille[1][6].setText("⋁");
                trickyGrille[1][6].setEditable(false);
                trickyGrille[1][14].setText("⋁");
                trickyGrille[1][14].setEditable(false);
                trickyGrille[2][2].setText("5");
                trickyGrille[2][2].setEditable(false);
                trickyGrille[3][6].setText("⋁");
                trickyGrille[3][6].setEditable(false);
                trickyGrille[4][0].setText("5");
                trickyGrille[4][0].setEditable(false);
                trickyGrille[4][1].setText(">");
                trickyGrille[4][1].setEditable(false);
                trickyGrille[4][12].setText("4");
                trickyGrille[4][12].setEditable(false);
                trickyGrille[5][6].setText("⋁");
                trickyGrille[5][6].setEditable(false);
                trickyGrille[5][12].setText("⋁");
                trickyGrille[5][12].setEditable(false);
                trickyGrille[5][14].setText("⋁");
                trickyGrille[5][14].setEditable(false);
                trickyGrille[6][9].setText("<");
                trickyGrille[6][9].setEditable(false);
                trickyGrille[7][0].setText("⋀");
                trickyGrille[7][0].setEditable(false);
                trickyGrille[7][8].setText("⋁");
                trickyGrille[7][8].setEditable(false);
                trickyGrille[8][3].setText("<");
                trickyGrille[8][3].setEditable(false);
                trickyGrille[8][5].setText(">");
                trickyGrille[8][5].setEditable(false);
                trickyGrille[8][12].setText("1");
                trickyGrille[8][12].setEditable(false);
                trickyGrille[8][14].setText("3");
                trickyGrille[8][14].setEditable(false);
                trickyGrille[9][14].setText("⋀");
                trickyGrille[9][14].setEditable(false);
                trickyGrille[11][8].setText("⋁");
                trickyGrille[11][8].setEditable(false);
                trickyGrille[11][12].setText("⋁");
                trickyGrille[11][12].setEditable(false);
                trickyGrille[12][12].setText("7");
                trickyGrille[12][12].setEditable(false);
                trickyGrille[13][6].setText("⋁");
                trickyGrille[13][6].setEditable(false);
                trickyGrille[14][2].setText("6");
                trickyGrille[14][2].setEditable(false);
                trickyGrille[14][14].setText("5");
                trickyGrille[14][14].setEditable(false);
               
                break;
        }
        return trickyGrille;
    }
}
