package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.Afficheur.PlanRoulotteDessineur;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOMurSeparateur;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.UUID;

public class MurSeparateur extends ElementSelectionnable implements Serializable {
    private Mesure epaisseurDado;
    private Mesure distanceDuPlancher;
    private Mesure distancePoutreArriere;
    private Point2D position;
    private UUID id;
    private Polygon polygon;
    private PlanRoulotte parent;
    private final int prioriteAffichage = 1;

    public MurSeparateur(PlanRoulotte parent) {
        super(false, false);
        this.epaisseurDado = new Mesure(0, 3, 4); // 3/4" d'épais
        this.distanceDuPlancher = new Mesure(0,0,0);
        this.distancePoutreArriere = new Mesure(0, 0, 0);
        this.polygon = new Polygon();
        this.parent = parent;
    }

    public MurSeparateur(Mesure epaisseurDado, Mesure distanceDuPlancher, Mesure distancePoutreArriere, PlanRoulotte parent) {
        super(false, false);
        this.epaisseurDado = epaisseurDado;
        this.distanceDuPlancher = distanceDuPlancher;
        this.distancePoutreArriere = distancePoutreArriere;
        this.polygon = new Polygon();
        this.parent = parent;
    }

    public void setDistanceDuPlancher(Mesure distanceDuPlancher) {
        this.distanceDuPlancher = distanceDuPlancher;
    }

    public void setDistancePoutreArriere(Mesure distancePoutreArriere) {
        this.distancePoutreArriere = distancePoutreArriere;
    }

    public void setEpaisseurDado(Mesure epaisseurDado) {
        this.epaisseurDado = epaisseurDado;
    }

    public Mesure getEpaisseurDado() {return epaisseurDado;}

    public Mesure getDistanceDuPlancher() {return distanceDuPlancher;}

    public Mesure getDistancePoutreArriere() {return distancePoutreArriere;}

    @Override
    public boolean isSelected() {return this.selectionStatus;}

    @Override
    public void translate(Point2D.Double delta) {

    }

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
    public DTOMurSeparateur getElementDTO() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return new DTOMurSeparateur(epaisseurDado, distanceDuPlancher, distancePoutreArriere, selectionStatus);
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
        Polygon polygoneMurSeparateur = new Polygon();
        int mmhauteurPanneau = parent.getDTOPanneau().getHauteurBrute().getMillimetres();
        int mmlargeurPanneau = parent.getDTOPanneau().getLargeurBrute().getMillimetres();
        int mmEpaisseurPlancher = parent.getDTOPlancher().getEpaisseur().getMillimetres();
        int mmDistancePoutreArr = this.distancePoutreArriere.getMillimetres();
        int mmDistancePlancher  = this.distanceDuPlancher.getMillimetres();
        int mmEpaisseur = this.epaisseurDado.getMillimetres();
        int arcXMax = Ellipse.getPtCoinSupGaucheFin().x;

        // Trouver la position maximum du mur séparateur
        Polygon copiePoutreArriere = new Polygon(parent.getPoutreArriere().getPolygon().xpoints, parent.getPoutreArriere().getPolygon().ypoints, parent.getPoutreArriere().getPolygon().npoints);

        int posXMaxPoutreArr = -1;

        for (int i=0; i<copiePoutreArriere.npoints; i++) {
            if (copiePoutreArriere.xpoints[i] > posXMaxPoutreArr) {posXMaxPoutreArr = copiePoutreArriere.xpoints[i];}
        }

        if(posXMaxPoutreArr + mmDistancePoutreArr >= parent.getDTOPlancher().getMargeArriere().getMillimetres()) {
            polygoneMurSeparateur.addPoint(posXMaxPoutreArr + mmDistancePoutreArr, 0);
            polygoneMurSeparateur.addPoint(posXMaxPoutreArr + mmDistancePoutreArr + mmEpaisseur, 0);
            polygoneMurSeparateur.addPoint(posXMaxPoutreArr + mmDistancePoutreArr + mmEpaisseur, mmhauteurPanneau - mmEpaisseurPlancher - mmDistancePlancher);
            polygoneMurSeparateur.addPoint(posXMaxPoutreArr + mmDistancePoutreArr, mmhauteurPanneau - mmEpaisseurPlancher - mmDistancePlancher);
        }
        else{
            Mesure nouvelleDistancePoutre = new Mesure(0,0,0);
            nouvelleDistancePoutre.setMetrique(parent.getDTOPlancher().getMargeArriere().getMillimetres() - posXMaxPoutreArr);

            this.distancePoutreArriere = nouvelleDistancePoutre;
            polygoneMurSeparateur.addPoint(parent.getDTOPlancher().getMargeArriere().getMillimetres(), 0);
            polygoneMurSeparateur.addPoint(parent.getDTOPlancher().getMargeArriere().getMillimetres() + mmEpaisseur, 0);
            polygoneMurSeparateur.addPoint(parent.getDTOPlancher().getMargeArriere().getMillimetres() + mmEpaisseur, mmhauteurPanneau - mmEpaisseurPlancher - mmDistancePlancher);
            polygoneMurSeparateur.addPoint(parent.getDTOPlancher().getMargeArriere().getMillimetres(), mmhauteurPanneau - mmEpaisseurPlancher - mmDistancePlancher);
        }
        this.polygon = polygoneMurSeparateur;
        this.dispositionValide = true;
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.MURSEPARATEUR;}
}
