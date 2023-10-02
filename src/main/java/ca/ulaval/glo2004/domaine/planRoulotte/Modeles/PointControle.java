package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.Afficheur.PlanRoulotteDessineur;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPointControle;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Point2D;
import java.io.Serializable;


public class PointControle extends ElementSelectionnable implements Serializable {

    private int num;
    private Mesure x;
    private Mesure y;
    private Point2D.Double point;
    private Ellipse2D.Double cercle;
    private Polygon polygon;
    private final int prioriteAffichage = 1;
    private double largeurHauteur = 40;
    private Plancher plancher;
    private Panneau panneau;

    public PointControle(int num, Plancher plancher, Panneau panneau) {
        super(false, false);
        this.num = num;
        this.plancher = plancher;
        this.panneau = panneau;
        setVariables();
    }

    private void setVariables() {
        Polygon plancherPoly = plancher.getPolygon();
        int[] xpoints = plancherPoly.xpoints;
        int[] ypoints = plancherPoly.ypoints;
        int premierX = xpoints[0];
        int dernierX = xpoints[1];
        int ancrageY = ypoints[3];
        double espacementPoints = (dernierX-premierX) /3;
        x = new Mesure(0,0,0);
        y = new Mesure(0,0,0);
        switch (num) {
            case 1:
                x.setMetrique(premierX);
                y.setMetrique(ancrageY);
                break;
            case 2:
                x.setMetrique(premierX + (int)espacementPoints);
                y.setMetrique(0);
                break;
            case 3:
                x.setMetrique(premierX + 2*(int)espacementPoints);
                y.setMetrique(0);
                break;
            case 4:
                x.setMetrique(dernierX);
                y.setMetrique(ancrageY);
                break;
        }
        point = new Point2D.Double(x.getMillimetres(),y.getMillimetres());
        cercle = new Ellipse2D.Double(x.getMillimetres()-largeurHauteur/2,y.getMillimetres()-largeurHauteur/2, largeurHauteur, largeurHauteur);
    }

    @Override
    public DTOElement getElementDTO() {
        return new DTOPointControle(this.selectionStatus);
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
        if (num == 1 || num == 4) {
            Polygon plancherPoly = plancher.getPolygon();
            int ancrageY = plancherPoly.ypoints[3];
            if (num == 4) {
                int dernierX = plancherPoly.xpoints[1];
                x.setMetrique(dernierX);
                y.setMetrique(ancrageY);
            } else {
                int premierX = plancherPoly.xpoints[0];
                x.setMetrique(premierX);
                y.setMetrique(ancrageY);
            }
            point = new Point2D.Double(x.getMillimetres(),y.getMillimetres());
            cercle = new Ellipse2D.Double(x.getMillimetres()-largeurHauteur/2,y.getMillimetres()-largeurHauteur/2, largeurHauteur, largeurHauteur);
        }

        this.polygon = PlanRoulotteDessineur.toPolygon(new FlatteningPathIterator(cercle.getPathIterator(null), 1));

        dispositionValide = true;
    }

    @Override
    public boolean isSelected() {
        return this.selectionStatus;
    }

    @Override
    public void translate(Point2D.Double delta) {
        if (num == 2 || num == 3) {
            boolean estValide = true;
            Polygon polyPanneau = panneau.getNextBezierPolygon(delta, num);
            int[] xPoints = polyPanneau.xpoints;
            int[] yPoints = polyPanneau.ypoints;
            int nPoints = polyPanneau.npoints;

            int largeurMm = panneau.getLargeurBrute().getMillimetres();
            int hauteurMm = panneau.getHauteurBrute().getMillimetres();

            for (int i = 0; i<nPoints;i++) {
                if (xPoints[i] < 0 || xPoints[i] > largeurMm) { estValide = false;}
                if (yPoints[i] < 0 || yPoints[i] > hauteurMm) { estValide = false;}
            }
            if (estValide) {
                x.setMetrique(x.getMillimetres() + (int) delta.getX());
                y.setMetrique(y.getMillimetres() + (int) delta.getY());
                point = new Point2D.Double(x.getMillimetres(),y.getMillimetres());
                cercle = new Ellipse2D.Double(x.getMillimetres()-largeurHauteur/2,y.getMillimetres()-largeurHauteur/2, largeurHauteur, largeurHauteur);
            }
        }
    }

    @Override
    public int getPrioriteAffichage() {
        return this.prioriteAffichage;
    }

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {
        return TypeElement.POINTCONTROLE;
    }

    @Override
    public void switchSelection() {
        this.selectionStatus = !this.selectionStatus;
    }

    @Override
    public boolean estInterne() {
        return false;
    }

    @Override
    public boolean estExterne() {
        return false;
    }

    public Point2D.Double getPoint() {
        return point;
    }
}
