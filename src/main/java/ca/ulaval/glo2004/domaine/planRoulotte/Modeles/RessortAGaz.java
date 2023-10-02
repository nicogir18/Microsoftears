package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTORessortAGaz;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class RessortAGaz extends ElementSelectionnable implements Serializable {

    private Mesure longueur;
    private int force;
    private Point positionDuHayon;
    private Point positionDuMur;
    private UUID id;
    private Polygon polygon;
    private PlanRoulotte parent;
    private int nbRessort;
    private Mesure longueurCompressee;
    private Mesure longueurExtensionnee;
    private Mesure strokeRessort;
    private String noModeleRessort;
    private final int prioriteAffichage = 1;

    public RessortAGaz(PlanRoulotte parent) {
        super(false, false);
        this.parent = parent;
        this.longueur = new Mesure(0,0,0);
        this.force = 50;
        this.positionDuHayon = new Point();
        this.positionDuMur = new Point();
        this.polygon = new Polygon();
        this.nbRessort = 2;
        this.longueurCompressee = new Mesure(0,0,0);
        this.longueurExtensionnee = new Mesure(0,0,0);
        this.strokeRessort = new Mesure(0,0,0);
        this.id = UUID.randomUUID();
        this.noModeleRessort = "";
    }

    public RessortAGaz(Mesure longueur, int force, Point positionDuHayon, Point positionDuMur, UUID id, PlanRoulotte parent, int nbRessort) {
        super(false, false);
        this.longueur = longueur;
        this.force = force;
        this.positionDuHayon = positionDuHayon;
        this.positionDuMur = positionDuMur;
        this.id = id;
        this.polygon = new Polygon(new int[]{0, this.longueur.getMillimetres(), this.longueur.getMillimetres(), 0}, new int[]{0,0,25,25}, 4);
        this.nbRessort = nbRessort;
        this.longueurCompressee = new Mesure(0,0,0);
        this.longueurExtensionnee = new Mesure(0,0,0);
        this.strokeRessort = new Mesure(0,0,0);
        this.noModeleRessort = "";
    }

    public Mesure getLongueur() {return longueur;}

    public int getForce() {return force;}

    public Point2D getPositionDuHayon() {return positionDuHayon;}

    public Point2D getPositionDuMur() {return positionDuMur;}

    public int getNbRessort() {return this.nbRessort;} // TODO : À AJOUTER AU UML
    public void setNbRessort(int nbRessort) {this.nbRessort = nbRessort;} // TODO : À AJOUTER AU UML

    @Override
    public boolean isSelected() {return this.selectionStatus;}

    @Override
    public void translate(Point2D.Double delta) {}

    @Override
    public void switchSelection() {this.selectionStatus = !this.selectionStatus;}

    @Override
    public boolean estInterne() {
        return false;
    }

    @Override
    public boolean estExterne() {
        return false;
    }

    @Override
    public DTORessortAGaz getElementDTO() {
        return new DTORessortAGaz(longueur, force, nbRessort, noModeleRessort, selectionStatus);
    }

    @Override
    public Polygon getPolygon() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return polygon;
    }

    @Override
    public boolean dispositionAJour() {
        return dispositionValide;
    }

    @Override
    public void invalideLaDisposition() {
        this.dispositionValide = false;
    }

    @Override
    public void calculDisposition() {
        Polygon polyRetour = new Polygon();
        double flatness = 0.1;
        int moitieLargueurAffichageRessort = (int)(25.4/2.0);

        // Je commence par appliquer les calculs afin de pouvoir
        // Permet de déterminer le ressort à choisir et toute les variables qui en découle
        setLongueurExtensionnee();
        int mmLongueurExtensionnee = this.longueurExtensionnee.getMillimetres();

        // Je détermine la force qui sera appliquée sur le ressort
        setForce();

        // Trouver la position idéale à partir de la charnière
        double positionIdealeTopRessort = getPositionIdeale(); // À partir du sommet du hayon
        Point pointTopHayon = new Point(parent.getHayon().getPointTopHayon());

        Ellipse2D.Double ellipseTopRessort = new Ellipse2D.Double(pointTopHayon.x-positionIdealeTopRessort, pointTopHayon.y-positionIdealeTopRessort, positionIdealeTopRessort*2,positionIdealeTopRessort*2);
        Ellipse2D.Double ellipseBotRessort = new Ellipse2D.Double(pointTopHayon.x-mmLongueurExtensionnee, pointTopHayon.y-mmLongueurExtensionnee, mmLongueurExtensionnee*2,mmLongueurExtensionnee*2);

        Polygon copieHayon = new Polygon(parent.getHayon().getPolygon().xpoints, parent.getHayon().getPolygon().ypoints, parent.getHayon().getPolygon().npoints);

        ArrayList<Point> alPointTop = new ArrayList<>();
        ArrayList<Point> alPointBot = new ArrayList<>();

        Point ptAvant = new Point(copieHayon.xpoints[0], copieHayon.ypoints[0]);

        for (int i=1; i<copieHayon.npoints; i++) {
            Point ptApres = new Point(copieHayon.xpoints[i], copieHayon.ypoints[i]);

            Point bufferPtIntersectionTopRessort = PlanRoulotte.trouvePointSurCourbe(ptAvant, ptApres, ellipseTopRessort.getPathIterator(null, flatness));
            Point bufferPtIntersectionBotRessort = PlanRoulotte.trouvePointSurCourbe(ptAvant, ptApres, ellipseBotRessort.getPathIterator(null, flatness));

            if (bufferPtIntersectionTopRessort != null) {
                alPointTop.add(bufferPtIntersectionTopRessort);
            }
            if (bufferPtIntersectionBotRessort != null) {
                alPointBot.add(bufferPtIntersectionBotRessort);
            }

            ptAvant.setLocation(ptApres);
        }

        Point ptIntersectionTopRessort = new Point(alPointTop.get(alPointTop.size()-1));
        Point ptIntersectionBotRessort = new Point(alPointBot.get(alPointBot.size()-1));

        double angle = Math.PI/2.0 - Math.atan2(Math.abs(ptIntersectionTopRessort.y-ptIntersectionBotRessort.y), Math.abs(ptIntersectionTopRessort.x-ptIntersectionBotRessort.x));

        double deltaX = Math.cos(angle)*moitieLargueurAffichageRessort;
        double deltaY = Math.sin(angle)*moitieLargueurAffichageRessort;

        polyRetour.addPoint((int)(ptIntersectionTopRessort.x-deltaX), (int)(ptIntersectionTopRessort.y-deltaY));
        polyRetour.addPoint((int)(ptIntersectionTopRessort.x+deltaX), (int)(ptIntersectionTopRessort.y+deltaY));
        polyRetour.addPoint((int)(ptIntersectionBotRessort.x+deltaX), (int)(ptIntersectionBotRessort.y+deltaY));
        polyRetour.addPoint((int)(ptIntersectionBotRessort.x-deltaX), (int)(ptIntersectionBotRessort.y-deltaY));

        this.longueur.setMetrique((int)ptIntersectionTopRessort.distance(ptIntersectionBotRessort));

        this.polygon = polyRetour;

        dispositionValide = true;
    }

    /**
     * Méthode servant à identifier la bonne longueur de ressort une fois allongée
    */
    private void setLongueurExtensionnee() { // TODO : À AJOUTER AU UML
        double longueurIdeale = 0.6 * getLongueurHayon();

        ArrayList<Double[]> listeLongeurRessort = new ArrayList<>();
        ArrayList<String> listeNumeroPieceRessort = new ArrayList<>();

        // Longueur basé selon le site web : https://www.mcmaster.com/gas-struts/gas-springs-7/
        // Premier   = Longueur étiré
        // Deuxième  = Longueur une fois compressé
        // Troisième = Le stroke du ressort
        // Quatrième = Numéro de pièce
        listeLongeurRessort.add(new Double[]{7.01,5.04,1.97,});     listeNumeroPieceRessort.add("4138T51");
        listeLongeurRessort.add(new Double[]{7.4,5.04,2.36});       listeNumeroPieceRessort.add("4138T52");
        listeLongeurRessort.add(new Double[]{9.65,6.11,3.54});      listeNumeroPieceRessort.add("4138T53");
        listeLongeurRessort.add(new Double[]{12.2,8.26,3.94});      listeNumeroPieceRessort.add("4138T54");
        listeLongeurRessort.add(new Double[]{13.19,8.27,4.92});     listeNumeroPieceRessort.add("4138T71");
        listeLongeurRessort.add(new Double[]{15.24,9.77,5.47});     listeNumeroPieceRessort.add("4138T55");
        listeLongeurRessort.add(new Double[]{17.13,10.83,6.3});     listeNumeroPieceRessort.add("4138T56");
        listeLongeurRessort.add(new Double[]{19.72,11.85,7.87});    listeNumeroPieceRessort.add("4138T57");
        listeLongeurRessort.add(new Double[]{20.12,11.85,8.27});    listeNumeroPieceRessort.add("4138T58");
        listeLongeurRessort.add(new Double[]{27.87,17.63,10.24});   listeNumeroPieceRessort.add("4138T61");
        listeLongeurRessort.add(new Double[]{29.49,16.69,12.8});    listeNumeroPieceRessort.add("4138T62");
        listeLongeurRessort.add(new Double[]{35.43,19.29,16.14});   listeNumeroPieceRessort.add("4138T63");

        int indexRessortSouhaite = -1;
        for (int i=0; i< listeLongeurRessort.size(); i++) {
            if (listeLongeurRessort.get(i)[0] > (longueurIdeale/25.4)) {
                indexRessortSouhaite = i;
                break;
            }
        }

        // Pour dans les cas où aucun ressort n'est disponible
        // Serait mieux un try-catch et gestin d'erreur -> Pas le temps
        if (indexRessortSouhaite<0) {indexRessortSouhaite=listeLongeurRessort.size()-1;}

        // J'applique les informations du ressort choisit
        setLongueurExtensionnee(listeLongeurRessort.get(indexRessortSouhaite)[0]);
        setLongueurCompressee(listeLongeurRessort.get(indexRessortSouhaite)[1]);
        setStrokeRessort(listeLongeurRessort.get(indexRessortSouhaite)[2]);

        setNoModeleRessort(listeNumeroPieceRessort.get(indexRessortSouhaite));
    }

    private String getNoModeleRessort() {return this.noModeleRessort;} // TODO : À AJOUTER AU UML
    private void setNoModeleRessort(String noModeleRessort) {this.noModeleRessort = noModeleRessort;} // TODO : À AJOUTER AU UML

    private Mesure getLongueurExtensionnee() {return this.longueurExtensionnee;} // TODO : À AJOUTER AU UML
    private void setLongueurExtensionnee(double longueurExtensionnee) {this.longueurExtensionnee.setMetrique((int)(longueurExtensionnee*25.4));} // TODO : À AJOUTER AU UML

    private Mesure getlongueurCompressee() {return this.longueurCompressee;} // TODO : À AJOUTER AU UML
    private void setLongueurCompressee(double longueurCompressee) {this.longueurCompressee.setMetrique((int)(longueurCompressee*25.4));} // TODO : À AJOUTER AU UML

    private double getLongueurHayon() { // TODO : À AJOUTER AU UML
        Point pointBotHayon = new Point(parent.getHayon().getPointBotHayon());
        Point pointTopHayon = new Point(parent.getHayon().getPointTopHayon());

        return pointBotHayon.distance(pointTopHayon);
    }

    private double getPositionIdeale() { // TODO : À AJOUTER AU UML
        return 0.85*this.strokeRessort.getMillimetres();
    }

    private Mesure getStrokeRessort() {return this.strokeRessort;} // TODO : À AJOUTER AU UML
    private void setStrokeRessort(double stroke) {this.strokeRessort.setMetrique((int)(stroke*25.4));} // TODO : À AJOUTER AU UML

    private void setForce() { // TODO : À AJOUTER AU UML
        double deadWeight = this.parent.getDTOHayon().getPoids()*4.4482216; // En Newton
        double centreGraviteLongueur = (getLongueurHayon())/2; // en mm
        double longueurBrasForce = getPositionIdeale(); // en mm
        double forceRequise = (deadWeight*centreGraviteLongueur)/(longueurBrasForce*this.nbRessort); // en Newton
        double sureteForceRequise = (deadWeight<300) ? forceRequise*0.1 : 50; // en Newton

        // Je ceil pour ajouter un safety
        this.force = (int)Math.ceil((forceRequise + sureteForceRequise)*0.224808943871);
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.RESSORTAGAZ;}
}
