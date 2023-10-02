package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D;
import java.io.Serializable;

public abstract class ElementSelectionnable implements Serializable {

    protected boolean dispositionValide;
    protected boolean selectionStatus;
    protected boolean estVisible = true;


    public enum TypeElement {
        ELLIPSE, PANNEAU, PLANCHER, AIDEAUDESIGN, HAYON, MURSEPARATEUR, OUVERTURELATERALE, POINTCONTROLE, POUTREARRIERE, RESSORTAGAZ, TOIT, NOTHING
    }

    public ElementSelectionnable(boolean dispositionValide, boolean selectionStatus){ //c'est un essai
        this.dispositionValide = dispositionValide;
        this.selectionStatus = selectionStatus;
    }

    public void setVisibilite() {
        estVisible = !estVisible;
    }
    public boolean estVisible() {
        return estVisible;
    }

    public abstract DTOElement getElementDTO();
    public abstract Polygon getPolygon();
    public abstract boolean dispositionAJour();
    public abstract void invalideLaDisposition();
    public abstract void calculDisposition();
    public abstract boolean isSelected();
    public abstract void translate(Point2D.Double delta);
    public abstract int getPrioriteAffichage();
    public abstract boolean contient(Point point);
    public abstract TypeElement getType();
    public abstract void switchSelection();
    public void setNonSelectionne(){
        selectionStatus = false;
    }
    public abstract boolean estInterne();
    public abstract boolean estExterne();
}
