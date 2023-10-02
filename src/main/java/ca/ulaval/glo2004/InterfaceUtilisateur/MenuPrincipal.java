package ca.ulaval.glo2004.InterfaceUtilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.EventObject;

import ca.ulaval.glo2004.InterfaceUtilisateur.listeners.*;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.ElementSelectionnable;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotteControleur;

public class MenuPrincipal extends JFrame implements Serializable {
    public PlanRoulotteControleur controleur;

    private ApplicationMode appMode;

    // Déclaration des components
    private JPanel fenetrePrincipale;

    private JMenuBar menuBar;

    private JMenu fileMenu;
    private JMenu nouveau;
    private JMenuItem nouveauElliptique;
    private JMenuItem nouveauBezier;
    private JMenuItem ouvrir;
    private JMenuItem enregistrer;
    private JMenuItem enregistrerSous;
    private JMenuItem exporter;
    private JMenuItem exporterJPG;
    private JMenuItem exporterSVG;

    private JMenu editMenu;
    private JMenu viewMenu;

    private JCheckBoxMenuItem afficherEllipses;
    private JCheckBoxMenuItem afficherOuvLat;
    private JCheckBoxMenuItem afficherAideDesign;
    private JCheckBoxMenuItem afficherToit;
    private JCheckBoxMenuItem afficherHayon;
    private JCheckBoxMenuItem afficherPlancher;
    private JCheckBoxMenuItem afficherPoutreArriere;
    private JCheckBoxMenuItem afficherRessortAGaz;
    private JCheckBoxMenuItem afficherMurSep;

    private JSplitPane splitPaneHor;
    private JScrollPane mainScrollPane;
    private DrawingPanel drawingPanel;

    private JPanel leftPanel;
    public JTabbedPane propertiesPane;
    private JTable propertiesTable;

    private JToolBar toolbar;

    private JPopupMenu addOuvLatMenu;
    private JMenuItem addFenetreMenuItem;
    private JMenuItem addPorteMenuItem;

    private JPopupMenu exportPanneau;
    private JMenuItem exportPanneauInterne;
    private JMenuItem exportPanneauExterne;

    private JPopupMenu addAideDesignMenu;
    private JMenuItem addLitMenuItem;
    private JMenuItem addEtagereMenuItem;
    private JMenuItem addPersonneMenuItem;

    private JButton undo;
    private UndoRedoHandler undoHandler;
    private UndoRedoHandler redoHandler;
    private JButton redo;
    private ButtonGroup actionGroup;
    private ButtonGroup uniteMesure;
    private JToggleButton select;
    private JToggleButton addOuvertureLaterale;
    private JButton addMurSeparateur;
    private JToggleButton addAideAuDesign;
    private JToggleButton delete;
    private JToggleButton grid;
    private JTextField espacement;
    private JLabel espacementLabel;
    private GrilleEspacementListener espacementListener;

    private JPanel leftBottomPanel;
    private JRadioButton uniteMesureMetrique;
    private JRadioButton uniteMesureImperial;
    private JLabel labelZoom;
    private JTextField textZoom;
    private JSeparator separatorPropriete;

    private Point2D.Double pointAjout;

    public enum ApplicationMode {
        SELECT, ADD_OUV_LAT, ADD_AIDE_DES, DELETE, EXPORT_PANNEAU
    }

    public MenuPrincipal() {
        controleur = new PlanRoulotteControleur(true);
        appMode = ApplicationMode.SELECT;
        initComponents();
    }

    private void initComponents() { // Executé à la création
        // Paramètres de la fenêtre
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Microsoftears");

        // Création du panel principal avec BorderLayout
        fenetrePrincipale = new JPanel(new BorderLayout());

        // Nouveau DrawingPanel (classe qui est un JPanel)
        drawingPanel = new DrawingPanel(this);
        drawingPanel.addMouseListener(new DrawingPanelMouse(this));
        drawingPanel.addMouseMotionListener(new DrawingPanelMouseMotion(this));
        drawingPanel.addMouseWheelListener(new DrawingPanelMouseWheel(this));

        //region Toolbar
        toolbar = new JToolBar();

        pointAjout = new Point2D.Double(0,0);

        // Création des boutons de la JToolbar (voir les 2 méthodes à fin de la classe)
        undo = makeButton("undo","Annuler");
        undoHandler = new UndoRedoHandler(this, true, undo);
        undo.addActionListener(undoHandler);
        undo.setEnabled(false);
        redo = makeButton("redo","Refaire");
        redoHandler = new UndoRedoHandler(this, false, redo);
        redo.addActionListener(redoHandler);
        redo.setEnabled(false);
        actionGroup = new ButtonGroup();
        select = makeToggleButton("select","Sélection");
        select.addActionListener(new SelectionModeAction(this));
        select.setSelected(true);
        addMurSeparateur = makeButton("ajouterSeparation","Ajouter un mur séparateur");
        addMurSeparateur.addActionListener(new AddMurSepModeAction(this));
        addOuvertureLaterale = makeToggleButton("ajouterFenetre","Ajouter une ouverture latérale");
        addOuvertureLaterale.addActionListener(new AddOuvLatModeAction(this));
        addAideAuDesign = makeToggleButton("ajouterLit","Ajouter une aide au design");
        addAideAuDesign.addActionListener(new AddAideDesModeAction(this));
        delete = makeToggleButton("delete","Supprimer");
        delete.addActionListener(new DeleteModeAction(this, select));
        grid = makeToggleButton("toggleGrille","Activer/Désactiver la grille");
        espacementLabel = new JLabel("Espacement : ");
        espacement = new JTextField("2 0/0");
        espacementListener = new GrilleEspacementListener(this, espacement);
        espacement.addActionListener(espacementListener);
        espacement.setMaximumSize(espacement.getPreferredSize());
        espacement.setEnabled(false);

        // Ajout des boutons à la toolbar
        toolbar.add(undo);
        toolbar.add(redo);
        toolbar.addSeparator();
        toolbar.add(select);
        toolbar.add(delete);
        toolbar.addSeparator();
        toolbar.add(addMurSeparateur);
        toolbar.addSeparator();
        toolbar.add(addOuvertureLaterale);
        toolbar.addSeparator();
        toolbar.add(addAideAuDesign);
        toolbar.addSeparator();
        toolbar.add(grid);
        toolbar.addSeparator();
        toolbar.add(espacementLabel);
        toolbar.add(espacement);

        addOuvLatMenu = new JPopupMenu();
        addFenetreMenuItem = new JMenuItem("Ajouter une fenêtre");
        addFenetreMenuItem.addActionListener(new AddOuvLatHandler(this, new Mesure(15,0,0), new Mesure(15,0,0), select));
        addPorteMenuItem = new JMenuItem("Ajouter une porte");
        addPorteMenuItem.addActionListener(new AddOuvLatHandler(this, new Mesure(20,0,0), new Mesure(45,0,0), select));
        addOuvLatMenu.add(addFenetreMenuItem);
        addOuvLatMenu.add(addPorteMenuItem);

        exportPanneau = new JPopupMenu();
        exportPanneauInterne = new JMenuItem("Exporter panneau interne");
        exportPanneauInterne.addActionListener(new ExportListener(this, true, this.controleur.isExportJPG()));
        exportPanneauExterne = new JMenuItem("Exporter panneau externe");
        exportPanneauExterne.addActionListener(new ExportListener(this, false, this.controleur.isExportJPG()));
        exportPanneau.add(exportPanneauInterne);
        exportPanneau.add(exportPanneauExterne);

        addAideDesignMenu = new JPopupMenu();
        addLitMenuItem = new JMenuItem("Ajouter un lit");
        addLitMenuItem.addActionListener(new AddAideDesHandler(this, new Mesure(36,0,0), new Mesure(18,0,0),select));
        addEtagereMenuItem = new JMenuItem("Ajouter une étagère");
        addEtagereMenuItem.addActionListener(new AddAideDesHandler(this, new Mesure(18,0,0), new Mesure(1,1,2), select));
        addPersonneMenuItem = new JMenuItem("Ajouter une personne");
        addPersonneMenuItem.addActionListener(new AddAideDesHandler(this, new Mesure(20,0,0), new Mesure(67,0,0), select));
        addAideDesignMenu.add(addLitMenuItem);
        addAideDesignMenu.add(addEtagereMenuItem);
        addAideDesignMenu.add(addPersonneMenuItem);

        // Ajout des ToggleButton au ActionGroup pour éviter de pouvoir toggle plusieurs boutons en même temps
        actionGroup.add(select);
        actionGroup.add(delete);
        actionGroup.add(addMurSeparateur);
        actionGroup.add(addOuvertureLaterale);
        actionGroup.add(addAideAuDesign);

        //Ajout de la toolbar à la fenêtre
        fenetrePrincipale.add(toolbar, BorderLayout.NORTH);
        //endregion

        //region MenuBar
        menuBar = new JMenuBar();

        //Onglet Fichier
        fileMenu = new JMenu("Fichier");
        nouveau = new JMenu("Nouveau");
        nouveauElliptique = new JMenuItem("Profil elliptique");
        nouveauElliptique.addActionListener(new NouveauProjetHandler(this, true));
        nouveauBezier = new JMenuItem("Profil Bézier");
        nouveauBezier.addActionListener(new NouveauProjetHandler(this, false));
        nouveau.add(nouveauElliptique);
        nouveau.add(nouveauBezier);

        ouvrir = new JMenuItem("Ouvrir");
        ouvrir.addActionListener(new OuvrirListener(this));
        enregistrer = new JMenuItem("Enregistrer");
        enregistrer.addActionListener(new SauvegarderListener(this, false));
        enregistrerSous = new JMenuItem("Enregistrer sous");
        enregistrerSous.addActionListener(new SauvegarderListener(this, true));
        exporter = new JMenu("Exporter");
        fileMenu.add(nouveau);
        fileMenu.add(ouvrir);
        fileMenu.add(enregistrer);
        fileMenu.add(enregistrerSous);
        fileMenu.add(exporter);
        exporterJPG = new JMenuItem("Exporter en JPG");
        exporterJPG.setName("JPG");
        exporterJPG.addActionListener(new ExportListener(this, false, true));
        exporterSVG = new JMenuItem("Exporter en SVG");
        exporterSVG.setName("SVG");
        exporterSVG.addActionListener(new ExportListener(this, false, false));
        exporter.add(exporterJPG);
        exporter.add(exporterSVG);

        //Ajout des listeners des boutons de l'onglet fichier
        nouveau.addActionListener(new NouveauProjetHandler(this, true));

        //Onglet Éditer
        editMenu = new JMenu("Éditer");

        //Onglet Affichage
        viewMenu = new JMenu("Affichage");
        afficherEllipses = new JCheckBoxMenuItem("Afficher les ellipses");
        afficherEllipses.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.ELLIPSE));
        afficherEllipses.setState(true);

        afficherAideDesign = new JCheckBoxMenuItem("Afficher les aides au design");
        afficherAideDesign.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.AIDEAUDESIGN));
        afficherAideDesign.setState(true);

        afficherOuvLat = new JCheckBoxMenuItem("Afficher les ouvertures latérales");
        afficherOuvLat.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.OUVERTURELATERALE));
        afficherOuvLat.setState(true);

        afficherToit = new JCheckBoxMenuItem("Afficher le toit");
        afficherToit.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.TOIT));
        afficherToit.setState(true);

        afficherHayon = new JCheckBoxMenuItem("Afficher le hayon");
        afficherHayon.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.HAYON));
        afficherHayon.setState(true);

        afficherPlancher = new JCheckBoxMenuItem("Afficher le plancher");
        afficherPlancher.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.PLANCHER));
        afficherPlancher.setState(true);

        afficherPoutreArriere = new JCheckBoxMenuItem("Afficher la poutre arrière");
        afficherPoutreArriere.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.POUTREARRIERE));
        afficherPoutreArriere.setState(true);

        afficherRessortAGaz = new JCheckBoxMenuItem("Afficher les ressorts à gaz");
        afficherRessortAGaz.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.RESSORTAGAZ));
        afficherRessortAGaz.setState(true);

        afficherMurSep = new JCheckBoxMenuItem("Afficher le mur séparateur");
        afficherMurSep.addActionListener(new AfficherDesafficherHandler(this, ElementSelectionnable.TypeElement.MURSEPARATEUR));
        afficherMurSep.setState(true);

        viewMenu.add(afficherEllipses);
        viewMenu.add(afficherAideDesign);
        viewMenu.add(afficherOuvLat);
        viewMenu.add(afficherToit);
        viewMenu.add(afficherHayon);
        viewMenu.add(afficherPlancher);
        viewMenu.add(afficherPoutreArriere);
        viewMenu.add(afficherRessortAGaz);
        viewMenu.add(afficherMurSep);



        //Ajout des onglets au menu
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        grid.addActionListener(new GrilleHandler(this));

        //Ajout du menu à la fenêtre
        setJMenuBar(menuBar);
        //endregion

        // Création du SplitPanel (propriétés et drawingpanel)
        splitPaneHor = new JSplitPane();

        //region DrawingPanel
        mainScrollPane = new JScrollPane(drawingPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        drawingPanel.scrollRectToVisible(new Rectangle(1100,1100,0,0));
        splitPaneHor.setRightComponent(mainScrollPane);
        //endregion

        //region Proprietes
        leftBottomPanel = new JPanel(new GridLayout(1,1));

        propertiesPane = new JTabbedPane();
        propertiesTable = new JTable() {
            @Override
            public boolean editCellAt(int row, int column, EventObject e) {
                boolean result = super.editCellAt(row, column, e);

                final Component editor = getEditorComponent();

                if (editor != null && editor instanceof JTextComponent) {
                    ((JTextComponent)editor).selectAll();

                    if (e == null)
                    {
                        ((JTextComponent)editor).selectAll();
                    }
                    else if (e instanceof MouseEvent)
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                ((JTextComponent)editor).selectAll();
                            }
                        });
                    }
                }
                return result;
            }
        };

        propertiesPane.setPreferredSize(new Dimension(300, 40));
        propertiesPane.addTab("Propriétés",null,new JScrollPane(propertiesTable),"");


        //endregion

        //region Propriétés du projet
        uniteMesureImperial = new JRadioButton("Impérial");
        uniteMesureMetrique = new JRadioButton("Métrique");
        uniteMesure = new ButtonGroup();

        uniteMesure.add(uniteMesureImperial);
        uniteMesure.add(uniteMesureMetrique);

        // Pour la gestion des unités
        uniteMesureImperial.addActionListener(new UniteHandler(this));
        uniteMesureMetrique.addActionListener(new UniteHandler(this));


        labelZoom = new JLabel("Zoom :");
        textZoom = new JTextField("100 %");
        textZoom.setSize(textZoom.getPreferredSize());
        separatorPropriete = new JSeparator();

        leftBottomPanel.setLayout(new BoxLayout(leftBottomPanel, BoxLayout.Y_AXIS)); // Pour que les items soient verticalement correctes
        leftBottomPanel.add(propertiesPane);
        leftBottomPanel.add(separatorPropriete);

        leftPanel = new JPanel();

        leftPanel.add(uniteMesureImperial);
        leftPanel.add(uniteMesureMetrique);
        leftPanel.add(labelZoom);
        leftPanel.add(textZoom);

        leftBottomPanel.add(leftPanel);

        splitPaneHor.setLeftComponent(leftBottomPanel);
        //endregion
        // Lier le contenu principal à la fenêtre
        fenetrePrincipale.add(splitPaneHor, BorderLayout.CENTER);

        //Layout de la fenêtre
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(fenetrePrincipale, GroupLayout.DEFAULT_SIZE, 1877, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(fenetrePrincipale, GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        revalidate();
        pack();
    }


    protected JButton makeButton(String imageName, String toolTipText) {
        JButton button = new JButton();
        button.setToolTipText(toolTipText);

        // Redimensionnement des images
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imageName+".png"),toolTipText);
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(30,30,java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newImg);

        button.setIcon(icon);
        return button;
    }

    protected JToggleButton makeToggleButton(String imageName, String toolTipText) {
        JToggleButton toggleButton = new JToggleButton();
        toggleButton.setToolTipText(toolTipText);

        // Redimensionnement des images
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imageName+".png"),toolTipText);
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(30,30,java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newImg);

        toggleButton.setIcon(icon);
        return toggleButton;
    }

    public DrawingPanel getDrawingPanel() {return drawingPanel;}

    public void updateZoom(double zoom) {
        zoom *= 100;
        String texteZoom = String.format("%.0f", zoom) + " %";
        textZoom.setText(texteZoom);
        textZoom.setSize(textZoom.getPreferredSize());
    }

    public void updateUnite(boolean EstImperiale) {
        uniteMesureMetrique.setSelected(!EstImperiale);
        uniteMesureImperial.setSelected(EstImperiale);
    }

    public void setAppMode(ApplicationMode mode) {
        appMode = mode;
    }

    public ApplicationMode getAppMode() {
        return appMode;
    }

    // TODO: Ajouter uml des fonctions jusqu'à la fin de la classe
    public void setPointAjout(Point2D pointAjout) {
        double zoomFacteur = controleur.getFacteurZoom();
        this.pointAjout.x = (this.pointAjout.getX() / zoomFacteur)*2-2500;
        this.pointAjout.y = (this.pointAjout.getY() / zoomFacteur)*2-2500;

    }

    public Point2D.Double getPointAjout(){
        return this.pointAjout;
    }

    public void showAddAideMenu(MouseEvent e) {
        addAideDesignMenu.show(drawingPanel, e.getX(), e.getY());
        pointAjout = new Point2D.Double(e.getX(), e.getY());
    }

    public void showAddOuvMenu(MouseEvent e) {
        addOuvLatMenu.show(drawingPanel, e.getX(), e.getY());
        pointAjout = new Point.Double(e.getX(), e.getY());
    }

    public void showExportPanneauMenu(MouseEvent e) {
        exportPanneau.show(drawingPanel, e.getX(), e.getY());
        pointAjout = new Point.Double(e.getX(), e.getY());
    }

    public void notifierUndoRedoBoutons() {
        undoHandler.refreshState();
        redoHandler.refreshState();
    }

    public void toggleGrille() {
        espacement.setEnabled(grid.isSelected());
    }

    public void refreshGrille() {
        espacementListener.loadData();
        espacement.setSize(espacement.getPreferredSize());
    }
}
