package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.awt.*;

public final class DTORessortAGaz extends DTOElement {
    private final Mesure _longueur;
    private final Double _force;
    private final boolean _selection;
    private final int _nbRessort;
    private final String _noModeleRessort;
    private final Polygon _polygon;


    public DTORessortAGaz(Mesure longueur, double force, int nbRessort,String noModeleRessort, boolean selection) {
        _longueur = longueur;
        _force = force;
        _selection = selection;
        _nbRessort = nbRessort;
        _noModeleRessort = noModeleRessort;
        _polygon = null; // a revoir le calcul
    }

    public Mesure getLongueur() {return _longueur;}
    public double getForce() {return _force;}
    public int getNbRessort() {return _nbRessort;} // TODO : À AJOUTER AU UML
    public String getNoModeleRessort() {return _noModeleRessort;} // TODO : À AJOUTER AU UML
    public boolean isSelected() {return _selection;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _longueur.setVariableImperiale(imperial);

        return new String[][]{
                {"Longueur", _longueur.toString()},
                {"Force", _force.toString()},
                {"Nombre ressort", String.valueOf(_nbRessort)},
                {"Modèle ressort", _noModeleRessort}
        };
    }

    @Override
    public String getNom() {
        return "Ressort à gaz";
    }
}
