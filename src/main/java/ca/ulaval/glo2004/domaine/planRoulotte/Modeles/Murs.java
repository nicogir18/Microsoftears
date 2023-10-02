package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPanneau;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.List;

public class Murs implements Serializable {

    private Panneau panneauInterieur;
    private Panneau panneauCentral;
    private Panneau panneauExterieur;
    private Profil profil;
    private List<OuvertureLaterale> ouvertureLateraleList;
    private PlanRoulotte parent;

    public Murs(List<Ellipse> ellipseListe, PlanRoulotte parent){
        Mesure largeur = new Mesure(96, 0, 0);
        Mesure hauteur = new Mesure(48, 0, 0);
        Mesure epaisseurPanneauInterieur = new Mesure(0, 1, 8);
        Mesure epaisseurPanneauExterieur = new Mesure(0, 1, 4);
        Mesure epaisseurPanneauCentral = new Mesure(0, 3, 4);
        this.panneauInterieur = new Panneau(largeur, hauteur, epaisseurPanneauInterieur, new Polygon(new int[]{0, 0, 2438, 2438}, new int[]{0, 1219, 1219, 0}, 4), parent);
        this.panneauExterieur = new Panneau(largeur, hauteur, epaisseurPanneauExterieur, new Polygon(new int[]{0, 0, 2438, 2438}, new int[]{0, 1219, 1219, 0}, 4), parent);
        this.panneauCentral = new Panneau(largeur, hauteur, epaisseurPanneauCentral, new Polygon(new int[]{0, 0, 2438, 2438}, new int[]{0, 1219, 1219, 0}, 4), parent);
        profil = new ProfilElliptique(ellipseListe);

        this.parent = parent;
    }

    public Murs(PlanRoulotte parent){
        Mesure largeur = new Mesure(96, 0, 0);
        Mesure hauteur = new Mesure(48, 0, 0);
        Mesure epaisseurPanneauInterieur = new Mesure(0, 1, 8);
        Mesure epaisseurPanneauExterieur = new Mesure(0, 1, 4);
        Mesure epaisseurPanneauCentral = new Mesure(0, 3, 4);
        this.panneauInterieur = new Panneau(largeur, hauteur, epaisseurPanneauInterieur, new Polygon(new int[]{0, 0, 2438, 2438}, new int[]{0, 1219, 1219, 0}, 4), parent);
        this.panneauExterieur = new Panneau(largeur, hauteur, epaisseurPanneauExterieur, new Polygon(new int[]{0, 0, 2438, 2438}, new int[]{0, 1219, 1219, 0}, 4), parent);
        this.panneauCentral = new Panneau(largeur, hauteur, epaisseurPanneauCentral, new Polygon(new int[]{0, 0, 2438, 2438}, new int[]{0, 1219, 1219, 0}, 4), parent);
        this.parent = parent;
    }

    public void ajouteOuvertureLaterale(OuvertureLaterale ouvertureLaterale){

        this.ouvertureLateraleList.add(ouvertureLaterale);

    }

    public DTOPanneau getDTOPanneau(){return panneauInterieur.getElementDTO();}

    public Panneau getPanneauInterieur(){return panneauInterieur;}

}
