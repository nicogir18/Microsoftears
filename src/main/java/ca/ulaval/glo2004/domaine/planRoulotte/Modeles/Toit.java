package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOToit;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

public class Toit extends ElementSelectionnable implements Serializable {

    private Mesure epaisseurDado;
    private PlanRoulotte parent;
    private Polygon polygon;
    private final int prioriteAffichage = 1;

    public Toit(PlanRoulotte parent) {
        super(false, false);
        this.epaisseurDado = new Mesure(2, 1, 4); // 2" 1/4 d'épaisseur - Section 2.6 document initial du TP
        this.parent = parent;
        this.polygon = new Polygon();
    }

    public Toit(Mesure epaisseurDado, PlanRoulotte parent) {
        super(false, false);
        this.epaisseurDado = epaisseurDado;
        this.parent = parent;
        this.polygon = new Polygon();
    }

    public void setEpaisseurDado(Mesure epaisseur){
        this.epaisseurDado = epaisseur;
    }

    public Mesure getEpaisseurDado() {return epaisseurDado;}

    @Override
    public void switchSelection() {this.selectionStatus = !this.selectionStatus;}

    @Override
    public boolean estInterne() {
        return false;
    }

    @Override
    public boolean estExterne() {
        return true;
    }

    @Override
    public DTOToit getElementDTO() {
        return new DTOToit(epaisseurDado, selectionStatus);
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
        double flatness = 0.1;
        int mmEpaisseur = this.epaisseurDado.getMillimetres();
        int mmHauteurRoulotte = parent.getDTOPanneau().getHauteurBrute().getMillimetres();
        int mmLargeurRoulotte = parent.getDTOPanneau().getLargeurBrute().getMillimetres();
        int mmEpaisseurPlancher = parent.getDTOPlancher().getEpaisseur().getMillimetres();

        Point ptFinToit = new Point();

        // Pour gérer quand le plancher est à gauche de l'ellipse
        int xPlancher = parent.getDTOPanneau().getLargeurBrute().getMillimetres() - parent.getDTOPlancher().getMargeAvant().getMillimetres();

        // Si nous avons un mur séparateur, il faut faire attention à partir de là.
        // Sinon on part de poutre arrière

        // Faire une copie du polygone
        Polygon poutreArriereCopie = new Polygon(parent.getPoutreArriere().getPolygon().xpoints, parent.getPoutreArriere().getPolygon().ypoints, parent.getPoutreArriere().getPolygon().npoints);

        int posXMaxPoutreArr = -1;

        for (int i=0; i<poutreArriereCopie.npoints; i++) {
            if (poutreArriereCopie.xpoints[i] > posXMaxPoutreArr) {posXMaxPoutreArr = poutreArriereCopie.xpoints[i];}
        }

        // ==============================
        // Gestion de la courbe extérieur
        // ==============================
        Polygon polyRetour = new Polygon();

        // Ajout du point supérieur droit de la poutre arrière. C'est le point à l'indice 1
        polyRetour.addPoint(poutreArriereCopie.xpoints[1], poutreArriereCopie.ypoints[1]);
        ptFinToit.setLocation(poutreArriereCopie.xpoints[2], mmEpaisseur);

        // Gestion pour quand on a un mur séparateur
        for (ElementSelectionnable element : parent.getElementsSelectionnables()) {
            if (element.getType() == ElementSelectionnable.TypeElement.MURSEPARATEUR) {
                // C'est qu'un mur séparateur existe
                polyRetour = new Polygon(); // J'efface car je n'aurais pas besoin du point de la poutre arrière

                int offsetPoutreArriere = parent.getDTOMurSeparateur().getDistancePoutreArriere().getMillimetres() + parent.getDTOMurSeparateur().getEpaisseurDado().getMillimetres();
                ptFinToit.setLocation(posXMaxPoutreArr+offsetPoutreArriere, mmEpaisseur);
                polyRetour.addPoint(posXMaxPoutreArr+offsetPoutreArriere, 0);
            }
        }

        if (posXMaxPoutreArr < Ellipse.getPtCoinSupGaucheFin().x) {
            Point pt1 = new Point(posXMaxPoutreArr, 0);
            Point pt2 = new Point(posXMaxPoutreArr, mmHauteurRoulotte);
            Polygon copieArc1 = new Polygon(Ellipse.getArc1().xpoints, Ellipse.getArc1().ypoints, Ellipse.getArc1().npoints);
            Point ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, copieArc1.getPathIterator(null, flatness));

            if (ptIntersection != null) {
                for (int i=0; i<copieArc1.npoints; i++) {
                    int x = copieArc1.xpoints[i];
                    int y = copieArc1.ypoints[i];
                    if (x<ptIntersection.x) {continue;}
                    polyRetour.addPoint(x, y);
                }
            }
        }

        // J'ajoute l'arc #2
        for (int i=0; i<Ellipse.getArc2().npoints; i++) {
            polyRetour.addPoint(Ellipse.getArc2().xpoints[i], Ellipse.getArc2().ypoints[i]);
        }


        for (int i=0; i<Ellipse.getArc3().npoints; i++) {
            polyRetour.addPoint(Ellipse.getArc3().xpoints[i], Ellipse.getArc3().ypoints[i]);
        }

        // ========================================
        // Gestion pour la courbe intérieur du toit
        // ========================================

        Polygon toitInt = new Polygon();

        Arc2D.Double arcInt2 = new Arc2D.Double();
        Arc2D.Double arcInt3 = new Arc2D.Double();

        for (Ellipse ellipse : parent.getEllipseListe()) {
            if (ellipse.getQuadrant() == 2) {
                arcInt2 = new Arc2D.Double(ellipse.getEllipse().getX()+mmEpaisseur, ellipse.getEllipse().getY()+mmEpaisseur, 2.0*(ellipse.getPolygon().getBounds().getWidth()-mmEpaisseur),2.0*(ellipse.getPolygon().getBounds().getHeight()-mmEpaisseur), 0, 90, Arc2D.OPEN);
            }

            if (ellipse.getQuadrant() == 3) {
                arcInt3 = new Arc2D.Double(ellipse.getEllipse().getX()+mmEpaisseur, ellipse.getEllipse().getY()+mmEpaisseur, 2.0*(ellipse.getPolygon().getBounds().getWidth()-mmEpaisseur),2.0*(ellipse.getPolygon().getBounds().getHeight()-mmEpaisseur), 0, -90, Arc2D.OPEN);
            }
        }

        // Arc intérieur 3 - Je commence par elle car c'est dans le sens des aiguilles d'une montre
        // Gestion des points limites
        Point pt1Debut = new Point(mmLargeurRoulotte-mmEpaisseur, 0);
        Point pt2Debut = new Point(mmLargeurRoulotte-mmEpaisseur, mmHauteurRoulotte);

        Point pt1Fin = new Point(0,mmHauteurRoulotte-mmEpaisseur);
        Point pt2Fin = new Point(mmLargeurRoulotte, mmHauteurRoulotte-mmEpaisseur);

        Point debut = PlanRoulotte.trouvePointSurCourbe(pt1Debut, pt2Debut, arcInt3.getPathIterator(null, flatness));
        Point fin   = PlanRoulotte.trouvePointSurCourbe(pt1Fin, pt2Fin, arcInt3.getPathIterator(null, flatness));

        PathIterator iterator = arcInt3.getPathIterator(null, flatness);
        float[] coordonnees = new float[6];
        int temoin2 = iterator.currentSegment(coordonnees);

        if (debut == null) {
            debut = new Point((int) coordonnees [0], (int) coordonnees [1]);
        }

        while (!iterator.isDone()) {
            int temoin = iterator.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            toitInt.addPoint(x, y);
            iterator.next();
        }

        if (fin == null) {
            // C'est que la courbe termine au dessus du plancher
            fin = new Point((int)arcInt3.x+(int)arcInt3.getWidth()/2, polyRetour.ypoints[polyRetour.npoints-1]);
        }


        // TODO : Check pour quand le plancher ets dans le toit (marge_avant <mmEpaisseur)
        if (Ellipse.getArc3().npoints>2) {
            polyRetour.addPoint(xPlancher, mmHauteurRoulotte);
            polyRetour.addPoint(xPlancher, mmHauteurRoulotte-mmEpaisseur);
            polyRetour.addPoint(fin.x, fin.y);

            for (int i = toitInt.npoints-1; i>=0; i--) {
                if ((toitInt.xpoints[i] > debut.x || toitInt.xpoints[i] < fin.x)
                        || (toitInt.ypoints[i] < debut.y || toitInt.ypoints[i] > fin.y)) {
                    continue;
                }
                if (toitInt.xpoints[i] > parent.getDTOPanneau().getLargeurBrute().getMillimetres()-mmEpaisseur) {continue;}
                polyRetour.addPoint(toitInt.xpoints[i], toitInt.ypoints[i]);
            }
            polyRetour.addPoint(parent.getDTOPanneau().getLargeurBrute().getMillimetres()-mmEpaisseur, debut.y);
        }
        else {
            // C'est que je n'ai pas de courbe en bas
            if (xPlancher > mmLargeurRoulotte-mmEpaisseur && xPlancher < mmLargeurRoulotte) { // C'est que le plancher fini dans l'épaisseur du toit, mais ne touche pas au coin
                polyRetour.addPoint(xPlancher, mmHauteurRoulotte);
                polyRetour.addPoint(xPlancher, mmHauteurRoulotte-mmEpaisseurPlancher);
                polyRetour.addPoint(mmLargeurRoulotte-mmEpaisseur, mmHauteurRoulotte-mmEpaisseurPlancher);
            }
            else if (xPlancher < mmLargeurRoulotte) { // Dans le cas où le plancher n'est pas sur le coin (margeAvant != 0)
                polyRetour.addPoint(mmLargeurRoulotte, mmHauteurRoulotte);
                polyRetour.addPoint(mmLargeurRoulotte-mmEpaisseur, mmHauteurRoulotte);
            }
            else {
                polyRetour.addPoint(mmLargeurRoulotte, mmHauteurRoulotte-parent.getDTOPlancher().getEpaisseur().getMillimetres());
                polyRetour.addPoint(mmLargeurRoulotte-mmEpaisseur, mmHauteurRoulotte-parent.getDTOPlancher().getEpaisseur().getMillimetres());
            }
        }

        // Arc intérieur 2
        toitInt.reset();
        pt1Debut = new Point(ptFinToit);
        pt2Debut = new Point(parent.getDTOPanneau().getLargeurBrute().getMillimetres(), ptFinToit.y);

        pt1Fin = new Point(parent.getDTOPanneau().getLargeurBrute().getMillimetres()-mmEpaisseur, 0);
        pt2Fin = new Point(parent.getDTOPanneau().getLargeurBrute().getMillimetres()-mmEpaisseur, parent.getDTOPanneau().getHauteurBrute().getMillimetres());

        debut = PlanRoulotte.trouvePointSurCourbe(pt1Debut, pt2Debut, arcInt2.getPathIterator(null, flatness));
        fin   = PlanRoulotte.trouvePointSurCourbe(pt1Fin, pt2Fin, arcInt2.getPathIterator(null, flatness));

        iterator = arcInt2.getPathIterator(null, flatness);
        coordonnees = new float[6];
        temoin2 = iterator.currentSegment(coordonnees);

        if (fin == null) {
            fin = new Point((int) coordonnees [0], (int) coordonnees [1]);
        }

        while (!iterator.isDone()) {
            int temoin = iterator.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            toitInt.addPoint(x, y);
            iterator.next();
        }

        if (debut == null) {
            debut = new Point((int) coordonnees [0], (int) coordonnees [1]);
        }

        if (Ellipse.getArc2().npoints>2) {

            polyRetour.addPoint(fin.x, fin.y);

            for (int i = 0; i<toitInt.npoints; i++) {
                if ((toitInt.xpoints[i] < debut.x || toitInt.xpoints[i] > fin.x)
                        || (toitInt.ypoints[i] < debut.y || toitInt.ypoints[i] > fin.y)) {
                    continue;
                }
                if (toitInt.xpoints[i] > parent.getDTOPanneau().getLargeurBrute().getMillimetres()-mmEpaisseur) {continue;}
                polyRetour.addPoint(toitInt.xpoints[i], toitInt.ypoints[i]);
            }
            polyRetour.addPoint(debut.x, debut.y);
        }
        else {
            // C'est que je n'ai pas de courbe en bas
            polyRetour.addPoint(parent.getDTOPanneau().getLargeurBrute().getMillimetres()-mmEpaisseur, mmEpaisseur);
        }

        // Ajout du point final (En fonction de si on a un mur séparateur)
        polyRetour.addPoint(ptFinToit.x, ptFinToit.y);

        // J'applique le nouveau polygone au toit
        this.polygon = polyRetour;
    }

    @Override
    public boolean isSelected() {
        return selectionStatus;
    }

    @Override
    public void translate(Point2D.Double delta) {

    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.TOIT;}
}
