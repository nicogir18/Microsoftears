package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPlancher;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;


public class Plancher extends ElementSelectionnable implements Serializable {
    private Mesure margeAvant;
    private Mesure margeArriere;
    private Mesure epaisseur;
    private int prioriteAffichage = 1;
    private Polygon polygon;
    private PlanRoulotte parent;



    public Plancher(Mesure margeAvant, Mesure margeArriere, Mesure epaisseur, PlanRoulotte parent){
        super(false, false);
        this.margeAvant = margeAvant;
        this.margeArriere = margeArriere;
        this.epaisseur = epaisseur;
        this.parent = parent;
        calculDisposition();
    }

    public Plancher(){
        super(false, false);
        //initialiser le reste des attributs de base
    }

    public Mesure getMargeAvant(){return margeAvant;}

    public Mesure getMargeArriere(){return margeArriere;}

    public Mesure getEpaisseur(){return epaisseur;}

    public void setMargeAvant(Mesure margeAvant){
        this.margeAvant = margeAvant;
    }

    public void setMargeArriere(Mesure margeArriere){
        this.margeArriere = margeArriere;
    }

    public void setEpaisseur(Mesure epaisseur){
        this.epaisseur = epaisseur;
    }

    @Override
    public boolean isSelected(){return this.selectionStatus;}

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
        return true;
    }

    @Override
    public DTOPlancher getElementDTO() {
        return new DTOPlancher(margeAvant, margeArriere, epaisseur, selectionStatus);
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
        Polygon polygonePlancher = new Polygon();
        int mmhauteurPanneau = parent.getDTOPanneau().getHauteurBrute().getMillimetres();
        int mmlargeurPanneau = parent.getDTOPanneau().getLargeurBrute().getMillimetres();
        int mmMargeAvant = this.margeAvant.getMillimetres();
        int mmMargeArriere = this.margeArriere.getMillimetres();

        int mmEpaisseur = this.epaisseur.getMillimetres();

        polygonePlancher.addPoint(mmMargeArriere,mmhauteurPanneau-mmEpaisseur);
        polygonePlancher.addPoint(mmlargeurPanneau - mmMargeAvant,mmhauteurPanneau-mmEpaisseur);
        polygonePlancher.addPoint(mmlargeurPanneau - mmMargeAvant,mmhauteurPanneau);
        polygonePlancher.addPoint(mmMargeArriere, mmhauteurPanneau);

        polygon = polygonePlancher;

        dispositionValide = true;
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.PLANCHER;}
}