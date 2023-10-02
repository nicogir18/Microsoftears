package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.awt.*;

public final class DTOPanneau extends DTOElement {
    private final Mesure _largeurBrute;
    private final Mesure _hauteurBrute;
    private final Mesure _epaisseur;
    private final boolean _selection;
    private final Polygon _polygon;

    public DTOPanneau(Mesure largeurBrute, Mesure hauteurBrute, Mesure epaisseur, boolean selection, Polygon polygon) {
        _largeurBrute = largeurBrute;
        _hauteurBrute = hauteurBrute;
        _epaisseur = epaisseur;
        _selection = selection;
        _polygon = polygon;

    }

    public Mesure getLargeurBrute(){return _largeurBrute;}
    public Mesure getHauteurBrute(){return _hauteurBrute;}
    public Mesure getEpaisseur() {return _epaisseur;}
    public boolean isSelected(){return _selection;}
    public Polygon getPolygon(){return _polygon;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _largeurBrute.setVariableImperiale(imperial);
        _hauteurBrute.setVariableImperiale(imperial);
        _epaisseur.setVariableImperiale(imperial);

        return new String[][]{
                {"Largeur", _largeurBrute.toString()},
                {"Hauteur", _hauteurBrute.toString()},
                {"Épaisseur", _epaisseur.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Panneau";
    }
}
