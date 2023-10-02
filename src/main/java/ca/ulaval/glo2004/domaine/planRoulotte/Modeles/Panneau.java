package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPanneau;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.List;

public class Panneau extends ElementSelectionnable implements Serializable {

    private Mesure largeurBrute;
    private Mesure hauteurBrute;
    private Mesure epaisseur;
    private Polygon polygon;
    private PlanRoulotte parent;
    private final int prioriteAffichage = 1;


    Panneau(Mesure largeurBrute, Mesure hauteurBrute, Mesure epaisseur, Polygon polygon, PlanRoulotte parent){
        super(false, false);
        this.largeurBrute = largeurBrute;
        this.hauteurBrute = hauteurBrute;
        this.epaisseur = epaisseur;
        this.polygon = polygon;
        this.parent = parent;
    }

    public Mesure getLargeurBrute(){return largeurBrute;}

    public Mesure getHauteurBrute(){return hauteurBrute;}

    public void setHauteurBrute(Mesure hauteurBrute) {
        this.hauteurBrute = hauteurBrute;
    }

    public void setLargeurBrute(Mesure largeurBrute){
        this.largeurBrute = largeurBrute;
    }

    public Mesure getEpaisseur() {return epaisseur;}

    @Override
    public boolean isSelected(){return this.selectionStatus;}

    @Override
    public void translate(Point2D.Double delta) {
    }

    @Override
    public void switchSelection() {this.selectionStatus = !this.selectionStatus;}

    @Override
    public boolean estInterne() {
        return true;
    }

    @Override
    public boolean estExterne() {
        return true;
    }


    @Override
    public DTOPanneau getElementDTO() {
        return new DTOPanneau(largeurBrute, hauteurBrute, epaisseur, selectionStatus, new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints));
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
        return this.dispositionValide;
    }

    @Override
    public void invalideLaDisposition() {
        this.dispositionValide = false;
    }

    @Override
    public void calculDisposition() {
        if (parent.getProfilType() == PlanRoulotte.PROFIL_TYPE.ELLIPTIQUE) {
            for (Ellipse ellipse : parent.getEllipseListe()) {
                ellipse.calculDisposition();
            }

            calculDispositionProfilElliptique(parent.getEllipseListe());

            Hayon hayon = parent.getHayon();
            Polygon polyTraiteDeScie = new Polygon(parent.getHayon().getPolyTraitDeScie().xpoints, parent.getHayon().getPolyTraitDeScie().ypoints, parent.getHayon().getPolyTraitDeScie().npoints);
            Polygon nouveauPanneau = new Polygon();

            PlanRoulotte.objectToPolygon(nouveauPanneau, Ellipse.getArc2().getPathIterator(null));
            PlanRoulotte.objectToPolygon(nouveauPanneau, Ellipse.getArc3().getPathIterator(null));
            PlanRoulotte.objectToPolygon(nouveauPanneau, polyTraiteDeScie.getPathIterator(null));

            this.polygon = nouveauPanneau;
        } else {
            for (PointControle p : parent.getPtsControleListe()) {
                p.calculDisposition();
            }
            polygon = calculDispositionProfilBezier(parent.getPtsControleListe());
        }
        dispositionValide = true;
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.PANNEAU;}

    public void calculDispositionProfilElliptique(List<Ellipse> listeEllipse) {
        Polygon polygoneRetour = new Polygon();

        for (Ellipse ellipseEtude : listeEllipse) {
            Polygon ellipse = ellipseEtude.getPolygonComplet();
            int quadrant = ellipseEtude.getQuadrant();

            Point pointSupGauche = new Point(0,0);
            Point pointInfGauche = new Point(0, this.getHauteurBrute().getMillimetres());
            Point pointSupDroit  = new Point(this.getLargeurBrute().getMillimetres(), 0);
            Point pointInfDroit  = new Point(this.getLargeurBrute().getMillimetres(), this.getHauteurBrute().getMillimetres());

            switch (quadrant) {
                case 1:
                    iterationSurEllipse(polygoneRetour, ellipseEtude.getQuarter1(ellipse,  pointInfGauche, pointSupGauche, pointSupDroit, true));
                    break;
                case 2:
                    iterationSurEllipse(polygoneRetour, ellipseEtude.getQuarter2(ellipse, pointSupGauche, pointSupDroit, pointInfDroit, true));
                    break;
                case 3:
                    iterationSurEllipse(polygoneRetour, ellipseEtude.getQuarter3(ellipse, pointSupDroit, pointInfDroit, pointInfGauche, true));
                    break;
                case 4:
                    iterationSurEllipse(polygoneRetour, ellipseEtude.getQuarter4(ellipse, pointInfDroit, pointInfGauche, pointSupGauche, true));
                    break;
            }
        }

        this.polygon = polygoneRetour;
    }

    private Polygon iterationSurEllipse(Polygon polygoneRetour, Polygon ellipse) {
        PathIterator iterEllipse = ellipse.getPathIterator(null, 1.1);
        float[] floats = new float[6];

        while(!iterEllipse.isDone()) {
            int type = iterEllipse.currentSegment(floats);
            int x = (int) floats[0];
            int y = (int) floats[1];

            if (type != PathIterator.SEG_CLOSE) {polygoneRetour.addPoint(x, y);}

            iterEllipse.next();
        }

        return polygoneRetour;
    }

    private Polygon calculDispositionProfilBezier(List<PointControle> ptsControle) {
        PointControle pt1 = ptsControle.get(0);
        PointControle pt2 = ptsControle.get(1);
        PointControle pt3 = ptsControle.get(2);
        PointControle pt4 = ptsControle.get(3);

        CubicCurve2D.Double curve = new CubicCurve2D.Double(pt1.getPoint().getX(), pt1.getPoint().getY(),pt2.getPoint().getX(), pt2.getPoint().getY(),
                pt3.getPoint().getX(), pt3.getPoint().getY(), pt4.getPoint().getX(), pt4.getPoint().getY());
        Polygon polyCurve = new Polygon();
        PlanRoulotte.objectToPolygon(polyCurve, curve.getPathIterator(null, 0.1));

        return polyCurve;
    }

    public Polygon getPolygonPanneauBrut() {
        int[] xpoints = new int[]{0,0, this.largeurBrute.getMillimetres(), this.largeurBrute.getMillimetres()};
        int[] ypoints = new int[]{0,hauteurBrute.getMillimetres(), hauteurBrute.getMillimetres(), 0};
        return new Polygon(xpoints, ypoints, 4);
    }

    public Polygon getNextBezierPolygon(Point2D.Double delta, int numPoint) {
        List<PointControle> liste = parent.getPtsControleListe();

        PointControle aChanger = liste.get(numPoint-1);
        double tempX = aChanger.getPoint().getX() + delta.getX();
        double tempY = aChanger.getPoint().getY() + delta.getY();

        Point2D.Double p1 = liste.get(0).getPoint();
        Point2D.Double p2 = liste.get(1).getPoint();
        Point2D.Double p3 = liste.get(2).getPoint();
        Point2D.Double p4 = liste.get(3).getPoint();

        CubicCurve2D.Double curve = new CubicCurve2D.Double();
        switch(numPoint) {
            case 2:
                curve = new CubicCurve2D.Double(p1.getX(), p1.getY(), tempX, tempY, p3.getX(), p3.getY(), p4.getX(), p4.getY());
                break;
            case 3:
                curve = new CubicCurve2D.Double(p1.getX(), p1.getY(),p2.getX(), p2.getY(), tempX, tempY, p4.getX(), p4.getY());
                break;
        }
        Polygon polyCurve = new Polygon();
        PlanRoulotte.objectToPolygon(polyCurve, curve.getPathIterator(null, 0.1));

        return polyCurve;
    }

}
