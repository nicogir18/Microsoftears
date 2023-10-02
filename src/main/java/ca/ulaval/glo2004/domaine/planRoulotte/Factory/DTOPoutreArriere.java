package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.awt.*;

public final class DTOPoutreArriere extends DTOElement {
    private final Mesure _largeur;
    private final Mesure _hauteur;
    private final boolean _selection;
    private final Point _position;
    private final Mesure _positionRelative;

    public DTOPoutreArriere(Mesure largeur, Mesure hauteur, Point position, boolean selection, Mesure positionRelative) {
        _largeur = largeur;
        _hauteur = hauteur;
        _selection = selection;
        _position = position;
        _positionRelative = positionRelative;
    }

    public Mesure getLargeur(){return _largeur;}
    public Mesure getHauteur(){return _hauteur;}
    public Point getPosition(){return _position;}
    public Mesure getPositionRelative(){return _positionRelative;}
    public boolean isSelected(){return _selection;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _largeur.setVariableImperiale(imperial);
        _hauteur.setVariableImperiale(imperial);

        return new String[][]{
                {"Largeur", _largeur.toString()},
                {"Hauteur", _hauteur.toString()},
                {"Position Relative", _positionRelative.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Poutre arrière";
    }
}
