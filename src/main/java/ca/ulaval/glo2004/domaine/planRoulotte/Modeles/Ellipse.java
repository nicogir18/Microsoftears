package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.Afficheur.PlanRoulotteDessineur;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOEllipse;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotteControleur;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.UUID;


public class Ellipse extends ElementSelectionnable implements Serializable {

    private Ellipse2D.Double ellipse;
    private final UUID id;
    private Polygon polygon;
    private Point2D.Double pointInitial;
    private Mesure largeur;
    private Mesure hauteur;
    private Mesure positionX;
    private Mesure positionY;
    private final int prioriteAffichage = 1;
    private final int quadrant; // 1=Sup Gauche, 2=Sup droit, 3=Inf Droit, 4=Inf Gauche
    private PlanRoulotte parent;
    public static PlanRoulotte parentStatic;
    //TODO: modifier la façon de gerer les arcs
    private static Polygon arc1 = new Polygon();
    private static Polygon arc2 = new Polygon();
    private static Polygon arc3 = new Polygon();
    private static Polygon arc4 = new Polygon();

    private static Point ptCoinSupGaucheDebut = new Point(0,0);
    private static Point ptCoinSupGaucheFin = new Point(0,0);

    private static Point ptCoinSupDroitDebut = new Point(0,0);
    private static Point ptCoinSupDroitFin = new Point(0,0);

    private static Point ptCoinInfGaucheDebut = new Point(0,0);
    private static Point ptCoinInfGaucheFin = new Point(0,0);

    private static Point ptCoinInfDroitDebut = new Point(0,0);
    private static Point ptCoinInfDroitFin = new Point(0,0);

    public Ellipse(Point2D.Double pointInitial,Mesure largeur, Mesure hauteur, int quadrant, UUID id, PlanRoulotte parent){
        super(false, false);
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.positionX = new Mesure(0,0,0);
        this.positionY = new Mesure(0,0,0);
        this.pointInitial = pointInitial;
        this.id = id;
        this.quadrant = quadrant;
        setPointInitial();
        this.ellipse = new Ellipse2D.Double(pointInitial.x,pointInitial.y,largeur.getMillimetres()*2,hauteur.getMillimetres()*2);
        this.parent = parent;
    }

    public Ellipse(Ellipse2D.Double ellipse, int quadrant, UUID id, PlanRoulotte parent){
        super(false, false);
        this.ellipse = ellipse;
        this.id = id;
        this.quadrant = quadrant;
        this.parent = parent;
    }


    public Ellipse() {
        super(false, false);
        this.ellipse = new Ellipse2D.Double();
        this.id = UUID.randomUUID();
        this.quadrant = -1;
    }

    public void setRedimensionPointInitial(int hauteur_PanneauBrut_Milimetre, int largeur_PanneauBrut_Milimetre) {
        switch (quadrant) {
            case 1:
                this.pointInitial.x = 0;
                this.pointInitial.y = 0;
                this.positionX.setMetrique(0);
                this.positionY.setMetrique(0);
                break;
            case 2:
                this.pointInitial.x = largeur_PanneauBrut_Milimetre - (largeur.getMillimetres()*2);
                this.pointInitial.y = 0;
                this.positionX.setMetrique(0);
                this.positionY.setMetrique(0);
                break;
            case 3:
                this.pointInitial.x = largeur_PanneauBrut_Milimetre - (largeur.getMillimetres()*2);
                this.pointInitial.y = hauteur_PanneauBrut_Milimetre - (hauteur.getMillimetres()*2);
                this.positionX.setMetrique(0);
                this.positionY.setMetrique(0);
                break;
            case 4:
                this.pointInitial.x = 0;
                this.pointInitial.y = hauteur_PanneauBrut_Milimetre - (hauteur.getMillimetres()*2);
                this.positionX.setMetrique(0);
                this.positionY.setMetrique(0);
                break;
        }
        this.ellipse = new Ellipse2D.Double(pointInitial.x + positionX.getMillimetres(),pointInitial.y + positionY.getMillimetres(),this.largeur.getMillimetres()*2,this.hauteur.getMillimetres()*2);
        invalideLaDisposition();
    }

    private void setPointInitial() {
        switch (quadrant) {
            case 1:
                break;
            case 2:
                pointInitial.x -= largeur.getMillimetres()*2;
                break;
            case 3:
                pointInitial.x -= largeur.getMillimetres()*2;
                pointInitial.y -= hauteur.getMillimetres()*2;
                break;
            case 4:
                pointInitial.y -= hauteur.getMillimetres()*2;
        }
    }

    public void editMesuresEllipse(Mesure x, Mesure y, Mesure w, Mesure h) {
        Mesure xMesure = x;
        Mesure yMesure = y;
        Mesure largeur = w;
        Mesure hauteur = h;
        int hauteurBrutePanneau = parent.getDTOPanneau().getHauteurBrute().getMillimetres();
        int largeurBrutePanneau = parent.getDTOPanneau().getLargeurBrute().getMillimetres();


        double differenceX = w.getMillimetres() - this.largeur.getMillimetres()*2;
        double differenceY = h.getMillimetres() - this.hauteur.getMillimetres()*2;

        // Repositionner le point de départ des ellipses pour ne pas ruiner le travail de l'utilisateur

        switch (quadrant) {
            case 1:
                break;
            case 2:
                this.pointInitial.x = largeurBrutePanneau - (w.getMillimetres()*2);
                break;
            case 3:
                this.pointInitial.x = largeurBrutePanneau - (w.getMillimetres()*2);
                this.pointInitial.y = hauteurBrutePanneau - (h.getMillimetres()*2);
                break;
            case 4:
                this.pointInitial.y = hauteurBrutePanneau - (h.getMillimetres()*2);
                break;
        }

        this.positionX = xMesure;
        this.positionY = yMesure;
        this.largeur = largeur;
        this.hauteur = hauteur;

        this.ellipse = new Ellipse2D.Double(pointInitial.getX()+positionX.getMillimetres(),
                pointInitial.getY()+positionY.getMillimetres(),w.getMillimetres()*2,h.getMillimetres()*2);

        invalideLaDisposition();
        parent.calculProfilElliptique();
    }

    public void modifierPointInitial(double differenceX, double differenceY) {
        switch (quadrant) {
            case 1:
                break;
            case 2:
                this.pointInitial.x -= differenceX - this.positionX.getMillimetres();
                break;
            case 3:
                this.pointInitial.x -= differenceX- this.positionX.getMillimetres();
                this.pointInitial.y -= differenceY - this.positionY.getMillimetres();
                break;
            case 4:
                this.pointInitial.y-= differenceY - this.positionY.getMillimetres();
                break;
        }
    }

    public void editEllipse(double x, double y, double w, double h) {
        this.ellipse = new Ellipse2D.Double(x,y,+w,h);
        invalideLaDisposition();
    }

    public void changeDims(Mesure x, Mesure y, Mesure w, Mesure h){
        this.positionX = x;
        this.positionY = y;
        this.hauteur = h;
        this.largeur = w;
        invalideLaDisposition();
    }

    public DTOEllipse getEllipseDTO() {
        Ellipse2D.Double ellipseTemp = new Ellipse2D.Double(ellipse.getX(),ellipse.getY(),ellipse.getWidth(),ellipse.getHeight());
        ellipse.setFrame(ellipse.getX(),ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());

        return new DTOEllipse(ellipseTemp, positionX, positionY,largeur ,hauteur);
    }

    public int getQuadrant() {return this.quadrant;
    }

    public UUID getId() {return id;}

    public Ellipse2D.Double getEllipse() {return ellipse;}

    @Override
    public boolean isSelected(){return this.selectionStatus;}


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
    public void translate(Point2D.Double delta){
        this.positionX.setMetrique(positionX.getMillimetres() + (int)delta.getX());
        this.positionY.setMetrique(positionY.getMillimetres() + (int) delta.getY());

        this.ellipse.x = (this.ellipse.getX() + delta.x);
        this.ellipse.y = (this.ellipse.getY() + delta.y);
    }

    @Override
    public DTOElement getElementDTO() {
        Ellipse2D.Double ellipseTemp = new Ellipse2D.Double();
        ellipse.setFrame(ellipse.getX(),ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());

        return new DTOEllipse(ellipseTemp, positionX, positionY,largeur ,hauteur, getId());
    }

    @Override
    public Polygon getPolygon() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return getQuartierADessiner();
    }

    // si on veut l'ellipse au complet pour intersections etc
    public Polygon getPolygonComplet() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return polygon;
    }

    private Polygon getQuartierADessiner() {
        // À retravailler pour mieux gérer les quadrants 2 et 3 (le dernier point que le pathiterator
        // parcourt est 2 fois dans la liste de points les quadrants ne fonctionnement pas tous pareil)

        int[] xPoints = polygon.xpoints;
        int[] yPoints = polygon.ypoints;
        int nPoints = polygon.npoints;
        int hauteur = (int)this.ellipse.getHeight();
        int largeur = (int)this.ellipse.getWidth();
        int x = (int)this.ellipse.getX();
        int y = (int)this.ellipse.getY();
        int xMin = 0;
        int xMax = 0;
        int yMin = 0;
        int yMax = 0;
        int xMilieu = x + (largeur/2);
        int yMilieu = y + (hauteur/2);

        // Variables min/max des quarts de cercle
        switch(quadrant) {
            case 1:
                xMin = x;
                xMax = x+(largeur/2);
                yMin = y;
                yMax = y+(hauteur/2);
                break;
            case 2:
                xMin = x+(largeur/2);
                xMax = x+largeur;
                yMin = y;
                yMax = y+(hauteur/2);
                break;
            case 3:
                xMin = x+(largeur/2);
                xMax = x+largeur;
                yMin = y+(hauteur/2);
                yMax = y+hauteur;
                break;
            case 4:
                xMin = x;
                xMax = x+(largeur/2);
                yMin = y+(hauteur/2);
                yMax = y+hauteur;
                break;
        }

        Polygon quartier = new Polygon();
        for (int i=0;i<nPoints;i++) {
            if (xPoints[i] >= xMin && xPoints[i] <= xMax) {
                if (yPoints[i] >= yMin && yPoints[i] <= yMax) {

                    quartier.addPoint(xPoints[i],yPoints[i]);
                }
            }
        }
        // Fonctionne mais à retravailler
        if (quadrant == 2 || quadrant == 3) {
            xPoints = quartier.xpoints;
            yPoints = quartier.ypoints;
            nPoints = quartier.npoints -2;
            quartier = new Polygon();

            switch (quadrant) {
                case 2:
                    // le 1er point du quadrant 2 est erronné
                    for (int i = 1; i <= nPoints; i++) {
                        quartier.addPoint(xPoints[i],yPoints[i]);
                    }
                    break;
                case 3:
                    for (int i = 0; i < nPoints; i++) {
                        quartier.addPoint(xPoints[i],yPoints[i]);
                    }
            }
        }
        // fermer la forme
        quartier.addPoint(xMilieu,yMilieu);

        return quartier;
    }

    @Override
    public boolean dispositionAJour() {
        return this.dispositionValide;
    }

    @Override
    public void invalideLaDisposition() {
        this.dispositionValide = false;
    }

    @Override
    public void calculDisposition() {
        // calculer le polygone
        this.polygon = PlanRoulotteDessineur.toPolygon(new FlatteningPathIterator(ellipse.getPathIterator(null), 1));

        dispositionValide = true;
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    public Polygon getQuarter1(Polygon ellipseCoin, Point coinInfGauchePanneau, Point coinSupGauchePanneau, Point coinSupDroitPanneau, boolean changerValeurbase){
        Polygon polyQuarter = new Polygon();
        int[] xpoints = ellipseCoin.xpoints;
        int[] ypoints = ellipseCoin.ypoints;

        for (int i = 0; i < ellipseCoin.npoints; i++){
            if (ellipse.x > coinSupGauchePanneau.x || ellipse.y > coinSupGauchePanneau.y) {break;} // Pour gérer quand l'ellipse est à l'intérieur du panneau.

            Point ptPrecedent = new Point(xpoints[i], ypoints[i]);

            int j = (i==0) ? i : i-1;
            Point ptSuivant = new Point(xpoints[j], ypoints[j]);

            Point ptIntersection1 = PlanRoulotteControleur.pointIntersection(ptPrecedent, ptSuivant, coinInfGauchePanneau,coinSupGauchePanneau);
            Point ptIntersection2 = PlanRoulotteControleur.pointIntersection(ptPrecedent, ptSuivant, coinSupGauchePanneau,coinSupDroitPanneau);

            if (xpoints[i] < coinSupGauchePanneau.x && (ptIntersection1 == null && ptIntersection2 == null)) {continue;} // Pour gérer les points qui sont en dehors du panneau en X
            if (ypoints[i] < coinSupGauchePanneau.y && (ptIntersection1 == null && ptIntersection2 == null)) {continue;} // Pour gérer les points qui sont en dehors du panneau en Y

            int ptX = ptPrecedent.x;
            int ptY = ptPrecedent.y;
            // && (ptIntersection1.x != 0 && ptIntersection1.y != 0)
            if (ptIntersection1 != null && polyQuarter.npoints == 0) {
                ptX = coinSupGauchePanneau.x;
                ptY = ptIntersection1.y;
                if (changerValeurbase) {ptCoinSupGaucheDebut.setLocation(ptX, ptY);}
            }
            if (ptIntersection2 != null && polyQuarter.npoints != 0) {
                polyQuarter.addPoint(ptIntersection2.x, coinSupGauchePanneau.y);
                if (changerValeurbase) {ptCoinSupGaucheFin.setLocation(ptIntersection2.x, coinSupGauchePanneau.y);}

                break;
            }

            if (ptX <= (ellipse.getX()+(ellipse.getWidth()/2))
                    && ptY <= Math.floor(ellipse.getY()+(ellipse.getHeight()/2))
                    && !(ptX == 0 && ptY == 0)
            ){
                polyQuarter.addPoint(ptX, ptY);
            }
        }

        // Pour quand on a l'ellipse en dedans du panneau, il faut bien un coin. Je lui passe donc seulement un point, le coin du panneau à l'étude
        if (polyQuarter.npoints <= 1) {
            polyQuarter.addPoint(coinSupGauchePanneau.x, coinSupGauchePanneau.y);

            if (changerValeurbase) {ptCoinSupGaucheDebut.setLocation(coinSupGauchePanneau.x, coinSupGauchePanneau.y);}
            if (changerValeurbase) {ptCoinSupGaucheFin.setLocation(coinSupGauchePanneau.x, coinSupGauchePanneau.y);}

        }

        if (changerValeurbase) {arc1 = new Polygon(polyQuarter.xpoints, polyQuarter.ypoints, polyQuarter.npoints);}

        return polyQuarter;
    }


    public Polygon getQuarter2(Polygon ellipseCoin, Point coinSupGauchePanneau, Point coinSupDroitPanneau, Point coinInfDroitPanneau, boolean changerValeurbase){
        Polygon polyQuarter = new Polygon();
        int[] xpoints = ellipseCoin.xpoints;
        int[] ypoints = ellipseCoin.ypoints;

        // SAM: premier point du 2e quart est en fait aussi le dernier, donc  on commence le for avec i à 1
        for (int i = 1; i<= ellipseCoin.npoints; i++){
            if (ellipse.x+ellipse.width < coinSupDroitPanneau.x || ellipse.y > coinSupDroitPanneau.y) {break;}

            Point ptEnCours = new Point(xpoints[i], ypoints[i]);

            int j = (i==0) ? i : i-1;
            Point ptPrecedent = new Point(xpoints[j], ypoints[j]);

            Point ptIntersection1 = PlanRoulotteControleur.pointIntersection(ptEnCours, ptPrecedent, coinSupGauchePanneau,coinSupDroitPanneau);
            Point ptIntersection2 = PlanRoulotteControleur.pointIntersection(ptEnCours, ptPrecedent, coinSupDroitPanneau,coinInfDroitPanneau);

            if (xpoints[i] > coinSupDroitPanneau.x && (ptIntersection1 == null && ptIntersection2 == null)) {continue;}
            if (ypoints[i] < coinSupDroitPanneau.y && (ptIntersection1 == null && ptIntersection2 == null)) {continue;}

            int ptX = ptEnCours.x;
            int ptY = ptEnCours.y;

            if (ptIntersection1 != null && polyQuarter.npoints == 0) {
                ptX = ptIntersection1.x;
                ptY = coinSupDroitPanneau.y;
                if (changerValeurbase) {ptCoinSupDroitDebut.setLocation(ptX, ptY);}
            }
            if (ptIntersection2 != null && polyQuarter.npoints != 0) {
                polyQuarter.addPoint(coinSupDroitPanneau.x, ptIntersection2.y);
                if (changerValeurbase) {ptCoinSupDroitFin.setLocation(coinSupDroitPanneau.x, ptIntersection2.y);}
                break;
            }

            if(ptX >= (ellipse.getX()+(ellipse.getWidth()/2))
                    && ptY <= Math.floor(ellipse.getY()+(ellipse.getHeight()/2))
                    && !(ptX == 0 && ptY == 0)
            ){
                polyQuarter.addPoint(ptX, ptY);
            }
        }

        if (polyQuarter.npoints <= 1) {
            polyQuarter.reset();
            polyQuarter.addPoint(coinSupDroitPanneau.x, coinSupDroitPanneau.y);
            if (changerValeurbase) {ptCoinSupDroitDebut.setLocation(coinSupDroitPanneau.x, coinSupDroitPanneau.y);}
            if (changerValeurbase) {ptCoinSupDroitFin.setLocation(coinSupDroitPanneau.x, coinSupDroitPanneau.y);}
        }

        if (changerValeurbase) {arc2 = new Polygon(polyQuarter.xpoints, polyQuarter.ypoints, polyQuarter.npoints);}

        return polyQuarter;
    }


    public Polygon getQuarter3(Polygon ellipseCoin, Point coinSupDroitPanneau, Point coinInfDroitPanneau, Point coinInfGauchePanneau, boolean changerValeurbase){
        Polygon polyQuarter = new Polygon();
        int[] xpoints = ellipseCoin.xpoints;
        int[] ypoints = ellipseCoin.ypoints;

        Arc2D arcTestCoin = new Arc2D.Double(ellipseCoin.getBounds().getX(), ellipseCoin.getBounds().getY(), ellipseCoin.getBounds().getWidth(), ellipseCoin.getBounds().getHeight(), 0, -90, Arc2D.PIE);

        if (arcTestCoin.contains(coinInfDroitPanneau) // Pour quand le coin est à l'intérieur de l'ellipse
                || coinInfDroitPanneau.y < arcTestCoin.getCenterY() // Pour quand le coin est plus haut que l'ellipse
                || coinInfDroitPanneau.y > arcTestCoin.getCenterY()+ (arcTestCoin.getHeight()/2) // Pour quand le coin est plus bas que l'ellipse
                || coinInfDroitPanneau.x < arcTestCoin.getCenterX() // Pour quand le coin est à droite de l,ellipse
                || coinInfDroitPanneau.x > arcTestCoin.getCenterX()+(arcTestCoin.getWidth()/2) // Pour quand il est à gauche de l'ellipse
        ) {
            // Si le coin du panneau est dans l'ellipse, c'est invalide.
            polyQuarter.addPoint(coinInfDroitPanneau.x, coinInfDroitPanneau.y);
            arc3 = new Polygon(polyQuarter.xpoints, polyQuarter.ypoints, polyQuarter.npoints);
            ptCoinInfDroitDebut = new Point(coinInfDroitPanneau.x, coinInfDroitPanneau.y);
            ptCoinInfDroitFin = new Point(coinInfDroitPanneau.x, coinInfDroitPanneau.y);
            return polyQuarter;
        }
        for (int i = 0; i < ellipseCoin.npoints; i++){
            if (ellipse.x+ellipse.width < coinInfDroitPanneau.x || ellipse.y+ellipse.height < coinInfDroitPanneau.y) {break;}

            Point ptEnCours = new Point(xpoints[i], ypoints[i]);

            int j = (i==0) ? i : i-1;
            Point ptPrecedent = new Point(xpoints[j], ypoints[j]);

            Point ptIntersection1 = PlanRoulotteControleur.pointIntersection(ptEnCours, ptPrecedent, coinSupDroitPanneau,coinInfDroitPanneau);
            Point ptIntersection2 = PlanRoulotteControleur.pointIntersection(ptEnCours, ptPrecedent, coinInfDroitPanneau,coinInfGauchePanneau);

            if (xpoints[i] > coinInfDroitPanneau.x && (ptIntersection1 == null && ptIntersection2 == null)) {continue;}
            if (ypoints[i] > coinInfDroitPanneau.y && (ptIntersection1 == null && ptIntersection2 == null)) {continue;}

            int ptX = ptEnCours.x;
            int ptY = ptEnCours.y;

            if (ptIntersection1 != null && polyQuarter.npoints == 0 && (ptIntersection1.x != 0 && ptIntersection1.y != 0)) {
                ptX = coinInfDroitPanneau.x;
                ptY = ptIntersection1.y;
                if (changerValeurbase) {ptCoinInfDroitDebut.setLocation(ptX, ptY);}
            }
            if (ptIntersection2 != null && polyQuarter.npoints != 0) {
                polyQuarter.addPoint(ptIntersection2.x, coinInfDroitPanneau.y);
                if (changerValeurbase) {ptCoinInfDroitFin.setLocation(ptIntersection2.x, coinInfDroitPanneau.y);}
                break;
            }

            if(ptX >= (ellipse.getX()+(ellipse.getWidth()/2))
                    //&& ptY >= Math.floor(ellipse.getY()+(ellipse.getHeight()/2))
                    && !(ptX == 0 && ptY == 0)
            ){
                if (ptY < ellipse.getY()+(ellipse.getHeight()/2)) {ptY = (int)(ellipse.getY()+(ellipse.getHeight()/2));}
                polyQuarter.addPoint(ptX, ptY);
            }
        }

        if (polyQuarter.npoints <= 1) {
            polyQuarter.addPoint(coinInfDroitPanneau.x, coinInfDroitPanneau.y);
            if (changerValeurbase) {ptCoinInfDroitDebut.setLocation(coinInfDroitPanneau.x, coinInfDroitPanneau.y);}
            if (changerValeurbase) {ptCoinInfDroitFin.setLocation(coinInfDroitPanneau.x, coinInfDroitPanneau.y);}
        }

        if (changerValeurbase) {arc3 = new Polygon(polyQuarter.xpoints, polyQuarter.ypoints, polyQuarter.npoints);}

        return polyQuarter;
    }


    public Polygon getQuarter4(Polygon ellipseCoin, Point coinInfDroitPanneau, Point coinInfGauchePanneau, Point coinSupGauchePanneau, boolean changerValeurbase){
        Polygon polyQuarter = new Polygon();
        int[] xpoints = ellipseCoin.xpoints;
        int[] ypoints = ellipseCoin.ypoints;

        for (int i = 0; i < ellipseCoin.npoints; i++){
            if (ellipse.x > coinInfGauchePanneau.x || ellipse.y+ellipse.height < coinInfGauchePanneau.y) {break;}

            Point ptEnCours = new Point(xpoints[i], ypoints[i]);

            int j = (i==0) ? i : i-1;
            Point ptPrecedent = new Point(xpoints[j], ypoints[j]);

            Point ptIntersection1 = PlanRoulotteControleur.pointIntersection(ptEnCours, ptPrecedent, coinInfDroitPanneau,coinInfGauchePanneau);
            Point ptIntersection2 = PlanRoulotteControleur.pointIntersection(ptEnCours, ptPrecedent, coinInfGauchePanneau,coinSupGauchePanneau);

            if (xpoints[i] < coinInfGauchePanneau.x && (ptIntersection1 == null && ptIntersection2 == null)) {continue;}
            if (ypoints[i] > coinInfGauchePanneau.y && (ptIntersection1 == null && ptIntersection2 == null)) {continue;}

            int ptX = ptEnCours.x;
            int ptY = ptEnCours.y;

            if (ptIntersection1 != null && polyQuarter.npoints == 0 && (ptIntersection1.x != 0 && ptIntersection1.y != 0)) {
                ptX = ptIntersection1.x;
                ptY = coinInfGauchePanneau.y;
                if (changerValeurbase) {ptCoinInfGaucheDebut.setLocation(ptX, ptY);}
            }
            if (ptIntersection2 != null && polyQuarter.npoints != 0) {
                polyQuarter.addPoint(coinInfGauchePanneau.x, ptIntersection2.y);
                if (changerValeurbase) {ptCoinInfGaucheFin.setLocation(coinInfGauchePanneau.x, ptIntersection2.y);}
                break;
            }

            if(ptX <= (ellipse.getX()+(ellipse.getWidth()/2))
                    && ptY >= Math.floor(ellipse.getY()+(ellipse.getHeight()/2))
                    && !(ptX == 0 && ptY == 0)
            ){
                polyQuarter.addPoint(ptX, ptY);
            }
        }

        if (polyQuarter.npoints <= 1) {
            polyQuarter.addPoint(coinInfGauchePanneau.x, coinInfGauchePanneau.y);
            if (changerValeurbase) {ptCoinInfGaucheDebut.setLocation(coinInfGauchePanneau.x, coinInfGauchePanneau.y);}
            if (changerValeurbase) {ptCoinInfGaucheFin.setLocation(coinInfGauchePanneau.x, coinInfGauchePanneau.y);}
        }

        if (changerValeurbase) {arc4 = new Polygon(polyQuarter.xpoints, polyQuarter.ypoints, polyQuarter.npoints);}

        return polyQuarter;
    }

    private Mesure getHauteur() {return this.hauteur;}
    private Mesure getLargeur() {return this.largeur;}


    @Override
    public TypeElement getType() {return TypeElement.ELLIPSE;}

    public static Point getPtCoinSupGaucheDebut() {return ptCoinSupGaucheDebut;}
    public static Point getPtCoinSupGaucheFin()   {return ptCoinSupGaucheFin;}
    public static Point getPtCoinSupDroitDebut()  {return ptCoinSupDroitDebut;}
    public static Point getPtCoinSupDroitFin()    {return ptCoinSupDroitFin;}
    public static Point getPtCoinInfDroitDebut()  {return ptCoinInfDroitDebut;}
    public static Point getPtCoinInfDroitFin()    {return ptCoinInfDroitFin;}
    public static Point getPtCoinInfGaucheDebut() {return ptCoinInfGaucheDebut;}
    public static Point getPtCoinInfGaucheFin()   {return ptCoinInfGaucheFin;}

    public static Polygon getArc1() {
        for (Ellipse ellipse : parentStatic.getEllipseListe()){
            if (!ellipse.dispositionAJour())
            ellipse.calculDisposition();
        }
        parentStatic.calculProfilElliptique();
        return arc1;}
    public static Polygon getArc2() {
        for (Ellipse ellipse : parentStatic.getEllipseListe()){
            if (!ellipse.dispositionAJour())
                ellipse.calculDisposition();
        }
        parentStatic.calculProfilElliptique();
        return arc2;}
    public static Polygon getArc3() {
        for (Ellipse ellipse : parentStatic.getEllipseListe()){
            if (!ellipse.dispositionAJour())
                ellipse.calculDisposition();
        }
        parentStatic.calculProfilElliptique();
        return arc3;}
    public static Polygon getArc4() {
        for (Ellipse ellipse : parentStatic.getEllipseListe()){
            if (!ellipse.dispositionAJour())
                ellipse.calculDisposition();
        }
        parentStatic.calculProfilElliptique();
        return arc4;}




}
