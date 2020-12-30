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
        // --- Initialisation des matrices des valeurs et des contraintes horizontales et verticales ---      
        this.dimension = dimension;
        dimGrille = 2 * dimension - 1;
        this.grille = grille;
        System.out.println("dimension de grille "+ dimGrille);
        if(level.equalsIgnoreCase("easy"))
        {
             this.grille = easy();
        }else if(level.equalsIgnoreCase("normal"))
        {
            this.grille = normal();
        }else{
            this.grille = tricky();
        }
       
        this.valGrille = new int[dimension][dimension];
        this.contraintesHoriz = new char[dimension][dimension - 1];
        this.contraintesVert = new char[dimension - 1][dimension];
        
        newGrille();
//          for (int i = 0; i < dimension; i++) {
//            for (int j = 0; j < dimension - 1; j++) {
//                System.out.println("*********" + contraintesHoriz[i][j]+" i "+i+" j "+j);
//            }
//        }
        
    }

    public int getDimension() {
        return dimension;
    }
    
    public void setSimpleBacktracking(boolean typeSolver)
    {
        simpleBacktracking=typeSolver;
    }
    public JTextField[][] getGrille() {
        return grille;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setGrille(JTextField[][] grille) {
        this.grille = grille;
        //  newGrille();
    }

    public boolean verifyContraintes(JTextField[][] grille, int dimension) {
        // --- On récupère et on vérifie ce qui est inséré dans la case ---
        for (int i = 0; i < dimGrille; i++) {
            for (int j = 0; j < dimGrille; j++) {
                if (!grille[i][j].getText().equals("")) {
                    if (i % 2 == 0 && j % 2 == 0) {
                        try {
                            int val = Integer.parseInt(grille[i][j].getText());
                            if (val < 1 || val > dimension) {
                                JOptionPane.showMessageDialog(null, "La valeur " + val + " insérée dans la cellule [" + i + ", " + j + "] n'appartient pas au domaine des valeurs possibles !");

                                return false;
                            }
                            valGrille[i / 2][j / 2] = val;
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Vous devez saisir un nombre dans la cellule [" + i + "," + j + "] !");
                            ex.printStackTrace();
                            return false;
                        }
                    } // --- Les contraintes horizontales : < et > ---
                    else if (i % 2 == 0 && j % 2 != 0) {
                        char contrHoriz = grille[i][j].getText().charAt(0);
                        if (!(contrHoriz == '<' || contrHoriz == '>')) {
                            JOptionPane.showMessageDialog(null, "Le signe insérée dans la cellule [" + (i + 1) + ", " + (j + 1) + "] n'est pas une contrainte (doit être '<' ou '>') !");
                            return false;
                        }
                        contraintesHoriz[i / 2][(j - 1) / 2] = contrHoriz;
                        // System.out.println("contrVert"+contrHoriz);
                    } // ---- Les contraintes verticales : ⋀ et ⋁ ---
                    else if (i % 2 != 0 && j % 2 == 0) {
                        char contrVert = grille[i][j].getText().charAt(0);
                        if (!(contrVert == '⋀' || contrVert == '⋁')) {
                            JOptionPane.showMessageDialog(null, "Le signe insérée dans la cellule [" + (i + 1) + ", " + (j + 1) + "] n'est pas une contrainte (doit être '^' ou 'v') !");
                            return false;
                        }
                        contraintesVert[(i - 1) / 2][j / 2] = contrVert;
                        System.out.println("contrVert" + contraintesVert[(i - 1) / 2][j / 2]);
                    }
                }
            }
        }

        // --- Vérification des contraintes entre colonnes et lignes ---
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int val = valGrille[i][j];
                if (val != 0) // Si la cellule contient un nombre
                {
                    // --- Comparaison de la valeur avec la colonne ---
                    for (int row = 0; row < dimension; row++) {
                        if (row != i) // Si on n'est pas dans la même ligne, pour ne pas comparer avec la même cellule
                        {
                            if (val == valGrille[row][j]) // Si on trouve une cellule contenant la même valeur
                            {
                                JOptionPane.showMessageDialog(null, "La valeur de la cellule [" + (i + 1) + ", " + (j + 1) + "] est doublé dans cette colonne (la cellule [" + (row + 1) + ", " + (j + 1) + "]) !");
                                return false;
                            }
                        }
                    }
                    // --- Comparaison de la valeur avec la ligne ---
                    for (int col = 0; col < dimension; col++) {
                        if (col != j) // Si on n'est pas dans la même colonne, pour ne pas comparer avec la même cellule
                        {
                            if (val == valGrille[i][col]) // Si on trouve un cellule contenant la même valeur
                            {
                                JOptionPane.showMessageDialog(null, "La valeur de la cellule [" + (i + 1) + ", " + (j + 1) + "] est doublé dans cette ligne (la cellule [" + (i + 1) + ", " + (col + 1) + "]) !");
                                return false;
                            }
                        }
                    }
                    // --- Vérification des signes entre les cellules horrizontes : > et < ---
                    /* --- Comparaison de la cellule avec la cellule à gauche ---*/
                    if (j != 0) // Puisque la grille des contraintes horizontales est de nbre de colonne = dimension - 1, j doit être >= 1
                    {
                        if (contraintesHoriz[i][j - 1] != ' ') // Si la case contient un signe
                        {

                            if (valGrille[i][j - 1] != 0) // Si la case à gauche contient un nombre
                            {
                                switch (contraintesHoriz[i][j - 1]) // Deux cas : '<' et '>'
                                {
                                    case '>':
                                        if (valGrille[i][j - 1] < val) // Si la valeur est inférieure à la cellule à gauche
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est > à la valeur " + valGrille[i][j - 1] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '<':
                                        if (valGrille[i][j - 1] > val) // Si la valeur est supérieure à la cellule à gauche
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est < à la valeur " + valGrille[i][j - 1] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    /* --- Comparaison de la cellule avec la cellule à droite ---*/
                    if (j != dimension - 1) // Puisque la grille des contraintes horizontales est de nbre de colonne = dimension - 1
                    {
                        if (contraintesHoriz[i][j] != ' ') // Si la case contient un signe
                        {

                            if (valGrille[i][j + 1] != 0) // Si la case à droite contient un nombre
                            {
                                switch (contraintesHoriz[i][j]) // Deux cas : '<' et '>'
                                {
                                    case '>':
                                        if (valGrille[i][j + 1] > val) // Si la valeur est inférieure à la cellule à droite
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est < à la valeur " + valGrille[i][j + 1] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '<':
                                        if (valGrille[i][j + 1] < val) // Si la valeur est supérieure à la cellule à droite
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est > à la valeur " + valGrille[i][j + 1] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i + 1) + ", " + (j) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    // --- Vérification des signes entre les cellules verticales : ⋀ et ⋁ ---
                    /* --- Comparaison de la cellule avec la cellule en haut ---*/
                    if (i != 0) // Puisque la grille des contraintes verticales est de nbre de ligne = dimension - 1, i doit être >= 1
                    {
                        if (contraintesVert[i - 1][j] != ' ') // Si la case contient un signe
                        {

                            if (valGrille[i - 1][j] != 0) // Si la case en haut contient un nombre
                            {
                                switch (contraintesVert[i - 1][j]) // Deux cas : '⋀' et '⋁'
                                {
                                    case '⋀':
                                        if (valGrille[i - 1][j] > val) // Si la valeur est inférieure à la cellule en haut
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est < à la valeur " + valGrille[i - 1][j] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '⋁':
                                        if (valGrille[i - 1][j] < val) // Si la valeur est supérieure à la cellule en haut
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est > à la valeur " + valGrille[i - 1][j] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    /* --- Comparaison de la cellule avec la cellule en bas ---*/
                    if (i != dimension - 1) // Puisque la grille des contraintes verticales est de nbre de ligne = dimension - 1
                    {
                        if (contraintesVert[i][j] != ' ') // Si la case contient un signe
                        {

                            if (valGrille[i + 1][j] != 0) // Si la case en bas contient un nombre
                            {
                                switch (contraintesVert[i][j]) // Deux cas : '⋀' et '⋁'
                                {
                                    case '⋀':
                                        if (valGrille[i + 1][j] < val) // Si la valeur est supérieure à la cellule en bas
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est > à la valeur " + valGrille[i + 1][j] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i) + ", " + (j + 1) + "]) !");
                                            return false;
                                        }
                                        break;
                                    case '⋁':
                                        if (valGrille[i + 1][j] > val) // Si la valeur est inférieure à la cellule en bas
                                        {
                                            JOptionPane.showMessageDialog(null, "La valeur " + val + " est > à la valeur " + valGrille[i + 1][j] + "\n (cellule [" + (i + 1) + ", " + (j + 1) + "] et [" + (i) + ", " + (j + 1) + "]) !");
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
        // --- Contraintes des lignes ---
        for (int i = 0; i < dimension; i++) // Ligne
        {
            for (int j = 0; j < dimension - 1; j++) // Colonne
            {
                for (int k = j + 1; k < dimension; k++) {
                    //System.out.println("i = " + i + ", j = " + j + ", k = " + k);
                    String val1 = "x" + i + "" + j;//String.valueOf(valGrille[i][j]);
                    String val2 = "x" + i + "" + k;//String.valueOf(valGrille[i][k]);
                    graph.addEdge(val1, val2);
                }
            }
        }
        // --- Contraintes des colonnes ---
        for (int i = 0; i < dimension; i++) { // Colonne
            for (int j = 0; j < dimension; j++) { // Ligne
                for (int k = j + 1; k < dimension; k++) {
                    //System.out.println("i = " + i + ", j = " + j + ", k = " + k);
                    String val1 = "x" + j + "" + i;//String.valueOf(valGrille[j][i]);
                    String val2 = "x" + k + "" + i;//String.valueOf(valGrille[k][i]);
                    graph.addEdge(val2, val1);
                }

                System.out.println("At " + i + "," + j);

                if (i > 0 && (contraintesVert[i - 1][j] == '⋀' || contraintesVert[i - 1][j] == '⋁')) {
                    System.out.println("Found contraites vert1 at " + i + "," + j + " = " + contraintesVert[i - 1][j]);

                    boolean cond = contraintesVert[i - 1][j] != '⋀';

                    String val1 = cond ? "s" + (i - 1) + "" + j : "s" + i + "" + j;
                    String val2 = cond ? "x" + i + "" + j : "x" + (i - 1) + "" + j;

                    graph.addEdge(val2, val1);

                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");

                    graph.addEdge(val1, val2);

                }

                if (i < dimension - 1 && (contraintesVert[i][j] == '⋀' || contraintesVert[i][j] == '⋁')) {
                    System.out.println("Found contraites vert2 at " + i + "," + j + " = " + contraintesVert[i][j]);

                    boolean cond = contraintesVert[i][j] != '⋀';

                    String val1 = cond ? "s" + i + "" + j : "s" + (i + 1) + "" + j;
                    String val2 = cond ? "x" + (i + 1) + "" + j : "x" + i + "" + j;

                    graph.addEdge(val2, val1);

                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");

                    graph.addEdge(val1, val2);
                }

                if (j < dimension - 1 && (contraintesHoriz[i][j] == '<' || contraintesHoriz[i][j] == '>')) {
                    System.out.println("Found contraites at " + i + "," + j + " = " + contraintesHoriz[i][j]);

                    boolean cond = contraintesHoriz[i][j] == '<';
                     //   System.out.println("/////////"+contraintesHoriz[i][j]);
                    String val1 = cond ? "s" + i + "" + (j + 1) : "s" + i + "" + j;
                    String val2 = cond ? "x" + i + "" + j : "x" + i + "" + ( j + 1 );

                    graph.addEdge(val2, val1);

                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");

                    graph.addEdge(val1, val2);
                }

                if (j > 0 && (contraintesHoriz[i][j - 1] == '<' || contraintesHoriz[i][j - 1] == '>')) {
                    System.out.println("Found horiz contraites at " + i + "," + j + " = " + contraintesHoriz[i][j - 1]);

                    boolean cond = contraintesHoriz[i][j - 1] == '<';

                    String val1 = cond ? "s" + i + "" + j : "s" + i + "" + ( j - 1);
                    String val2 = cond ? "x" + i + "" + ( j - 1) : "x" + i + "" + j;

                    graph.addEdge(val2, val1);

                    val1 = val1.replace("s", "x");
                    val2 = val2.replace("x", "i");

                    graph.addEdge(val1, val2);
                }
            }
        }

        // --- Table des domaines ---
        domainTable = new ST<String, SET<String>>();
        // --- Remplissage des domaines ---
        Object[][] domains = new Object[dimension][dimension];
        // --- Initialisation des domaines --- 
        for (int i = 0; i < dimension; i++) // Colonne
        {
            for (int j = 0; j < dimension; j++) // Ligne
            {
                domains[i][j] = new SET<String>();
            }
        }
        // --- Attribuer les domaines aux valeurs de la grille (1) : sans considérer les contraintes de signes ---
        for (int i = 0; i < dimension; i++) // Colonne
        {
            for (int j = 0; j < dimension; j++) // Ligne
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
        // --- Ajout des domaines à la table ---
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                domainTable.put("x" + i + "" + j, ((SET<String>) domains[i][j]));
            }
        }
        // --- Affichage des domaines de chaque cellule ---
        System.out.println("\nLa table des domaines est : ");
        Set<String> keys = (Set<String>) domainTable.getST().keySet();
        for (String key : keys) {
            System.out.println("Le domaine de " + key + " est: " + domainTable.getST().get(key));
        }
        // --- Configuration initiale ---
        config = new ST<String, String>();
        for (int i = 0; i < dimension; i++) // Ligne 
        {
            for (int j = 0; j < dimension; j++) // Colonne
            {
                this.config.put("x" + i + "" + j, ""); // Variables non affectées
            }
        }

        return domainTable;
    }

    public ST<String, String> solve(ArrayList<String> var) {
        choices=var;
        // --- Appliquer l'algorithme du Backtracking pour calculer le solution ---
        Backtracking backtracking = new Backtracking();
        domainTable = getDomain();
        System.out.println("Constraints: ");
        System.out.println(graph);
        
        ST<String, String> result = backtracking.backtracking(this.config, domainTable, graph,choices);

        return result;

    }

    public void newGrille() {
        for (int i = 0; i < dimGrille; i++) {
            for (int j = 0; j < dimGrille; j++) {
                if (!grille[i][j].getText().equals("")) {
                    if (i % 2 == 0 && j % 2 == 0) {
                        int val = Integer.parseInt(grille[i][j].getText());
                        valGrille[i / 2][j / 2] = val;
                    } else if (i % 2 == 0 && j % 2 != 0) {
                        char contrHoriz = grille[i][j].getText().charAt(0);
                        contraintesHoriz[i / 2][ (j - 1) /2] = contrHoriz;
                        System.out.println(" " + contrHoriz + " i " + i + " j " + j);
                    } else if (i % 2 != 0 && j % 2 == 0) {
                        char contrVert = grille[i][j].getText().charAt(0);
                        contraintesVert[(i - 1) / 2][j / 2] = contrVert;
                        System.out.println(" " + contrVert + " i " + i + " j ");
                    }
                }
            }
        }
    }
//    public ArrayList<String> chooseAlgo()
//    {
//        ArrayList<String> choice = new ArrayList<String>();
//        
//        String[] variableChoice = {"Simple","Degree","MRV",};
//        String[] domain= {"Simple","LCV"};
//        String[] amelior={"forward checking","AC-1"};
//        JComboBox jvC = new JComboBox(variableChoice);
//        JComboBox jcD = new JComboBox(domain);
//        JComboBox jcA = new JComboBox(amelior);
//
//        jvC.setEditable(true);
//        jcD.setEditable(true);
//        jcA.setEditable(true);
//
//        //create a JOptionPane
//        Object[] options = new Object[] {};
//        JOptionPane jop = new JOptionPane("choisir la methode de résolution",
//                                        JOptionPane.QUESTION_MESSAGE,
//                                        JOptionPane.DEFAULT_OPTION);
//        
//        //add combos to JOptionPane
//        jop.add(jvC);
//        jop.add(jcD);
//        jop.add(jcA);
//
//        //create a JDialog and add JOptionPane to it 
//        JDialog diag = new JDialog();
//        diag.getContentPane().add(jop);
//        diag.pack();
//        diag.setVisible(true);
//        choice.add(jvC.getSelectedItem().toString());
//        choice.add(jcD.getSelectedItem().toString());
//        choice.add(jcA.getSelectedItem().toString());
//    
//        return choice;
//    }
     
    public JTextField[][] easy()
    {
        JTextField[][] easyGrille = new JTextField[dimGrille][dimGrille];
        
        return easyGrille;
    }
    
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
                normalGrille[9][8].setEditable(false);
                normalGrille[10][1].setText("<");
                normalGrille[10][1].setEditable(false);
                normalGrille[10][4].setText("4");
                normalGrille[10][4].setEditable(false);
                normalGrille[10][5].setText(">");
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
                normalGrille[6][1].setText("<");
                normalGrille[6][1].setEditable(false);
                normalGrille[6][9].setText("<");
                normalGrille[6][9].setEditable(false);
                normalGrille[6][12].setText("7");
                normalGrille[6][12].setEditable(false);
                normalGrille[7][4].setText("⋁");
                normalGrille[7][4].setEditable(false);
                normalGrille[7][6].setText("⋀");
                normalGrille[7][6].setEditable(false);
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
                normalGrille[11][2].setText("⋀");
                normalGrille[11][2].setEditable(false);
                normalGrille[11][4].setText("⋁");
                normalGrille[11][4].setEditable(false);
                normalGrille[11][12].setText("⋀");
                normalGrille[11][12].setEditable(false);
                normalGrille[12][7].setText(">");
                normalGrille[12][7].setEditable(false);
                
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
                normalGrille[2][9].setText(">");
                normalGrille[2][9].setEditable(false);
                normalGrille[2][13].setText("<");
                normalGrille[2][13].setEditable(false);
                normalGrille[3][2].setText("⋀");
                normalGrille[3][2].setEditable(false);
                normalGrille[3][6].setText("⋁");
                normalGrille[3][6].setEditable(false);
                normalGrille[3][8].setText("⋁");
                normalGrille[3][8].setEditable(false);
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
                normalGrille[5][14].setText("⋀");
                normalGrille[5][14].setEditable(false);
                normalGrille[6][1].setText("<");
                normalGrille[6][1].setEditable(false);
                normalGrille[6][3].setText(">");
                normalGrille[6][3].setEditable(false);
                normalGrille[6][11].setText("<");
                normalGrille[6][11].setEditable(false);
                normalGrille[6][12].setText("5");
                normalGrille[6][12].setEditable(false);
                normalGrille[10][6].setText("6");
                normalGrille[10][6].setEditable(false);
                normalGrille[11][2].setText("⋀");
                normalGrille[11][2].setEditable(false);
                normalGrille[11][6].setText("⋁");
                normalGrille[11][6].setEditable(false);
                normalGrille[12][3].setText("<");
                normalGrille[12][3].setEditable(false);
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
   
     public JTextField[][] tricky()
    {
        JTextField[][] trickyGrille = new JTextField[dimGrille][dimGrille];
        
        return trickyGrille;
    }
}
