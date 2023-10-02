package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOOuvertureLaterale;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.UUID;

public class OuvertureLaterale extends ElementSelectionnable implements Serializable {

    private Mesure largeur;
    private Mesure hauteur;
    private Mesure positionX;
    private Mesure positionY;
    private Panneau panneau;
    private final int prioriteAffichage = 1;
    private Polygon polygon;
    private UUID id;


    public OuvertureLaterale(Mesure largeur, Mesure hauteur, Point2D position, Panneau panneau){
        super(false, true);
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.positionX = new Mesure(0,0,0);
        this.positionX.setMetrique((int)position.getX());
        this.positionY = new Mesure(0,0,0);
        this.positionY.setMetrique((int)position.getY());
        this.id = UUID.randomUUID();
        this.polygon = new Polygon(new int[]{(int)position.getX(),(int)position.getX(),(int)position.getX() + this.largeur.getMillimetres(),(int)position.getX() + this.largeur.getMillimetres()},
                new int[]{(int)position.getY(), (int)position.getY()+this.hauteur.getMillimetres(),(int)position.getY()+this.hauteur.getMillimetres(),(int)position.getY()},
                4);
        this.panneau = panneau;
    }

    public Mesure getLargeur() {return largeur;}
    public Mesure getHauteur() {return hauteur;}

    @Override
    public boolean isSelected() {return selectionStatus;}

    @Override
    public void translate(Point2D.Double delta) {
        Polygon panneauPoly = panneau.getPolygon();
        int tempX = positionX.getMillimetres() +(int)delta.getX();
        int tempY = positionY.getMillimetres() + (int)delta.getY();
        Polygon aideAuDesignTempPoly = new Polygon(new int[]{tempX,tempX,tempX + this.largeur.getMillimetres(),tempX + this.largeur.getMillimetres()},
                new int[]{tempY, tempY+this.hauteur.getMillimetres(),tempY+this.hauteur.getMillimetres(),tempY},
                4);

        // check si l'aide au design est dans le polygone panneau
        boolean result = true;
        int[] xpoints = aideAuDesignTempPoly.xpoints;
        int[] ypoints = aideAuDesignTempPoly.ypoints;

        for (int i = 0; i < aideAuDesignTempPoly.npoints; i++) {
            result = panneauPoly.contains(new Point(xpoints[i], ypoints[i]));
            if (!result) break;
        }

        if (result) {
            this.positionX.setMetrique(tempX);
            this.positionY.setMetrique(tempY);
            invalideLaDisposition();
        }
    }

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
    public DTOElement getElementDTO() {
        return new DTOOuvertureLaterale(largeur,hauteur,positionX, positionY,selectionStatus);
    }

    @Override
    public Polygon getPolygon() {
        if (!dispositionAJour()) {
            calculDisposition();
        }
        return polygon;
    }

    @Override
    public boolean dispositionAJour() {
        return false;
    }

    @Override
    public void invalideLaDisposition() {
        this.dispositionValide = false;
    }

    @Override
    public void calculDisposition() {
        this.polygon = new Polygon(new int[]{positionX.getMillimetres(),positionX.getMillimetres(),positionX.getMillimetres() + this.largeur.getMillimetres(),positionX.getMillimetres() + this.largeur.getMillimetres()},
                new int[]{positionY.getMillimetres(), positionY.getMillimetres()+this.hauteur.getMillimetres(),positionY.getMillimetres()+this.hauteur.getMillimetres(),positionY.getMillimetres()},
                4);
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.OUVERTURELATERALE;}

    public void setLargeur(Mesure largeur) {
        this.largeur = largeur;
    }

    public void setHauteur(Mesure hauteur) {
        this.hauteur = hauteur;
    }

    public void setPositionX(Mesure positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(Mesure positionY) {
        this.positionY = positionY;
    }
}
