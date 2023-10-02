package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

public final class DTOOuvertureLaterale extends DTOElement {
    private final Mesure _largeur;
    private final Mesure _hauteur;
    private final Mesure _positionX;
    private final Mesure _positionY;
    private final boolean _selection;

    public DTOOuvertureLaterale(Mesure largeur, Mesure hauteur, Mesure positionX, Mesure positionY, boolean selection) {
        _largeur = largeur;
        _hauteur = hauteur;
        _positionX = positionX;
        _positionY = positionY;
        _selection = selection;
    }


    public Mesure getLargeur() {return _largeur;}
    public Mesure getHauteur() {return _hauteur;}
    public boolean isSelected() {return _selection;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _largeur.setVariableImperiale(imperial);
        _hauteur.setVariableImperiale(imperial);
        _positionX.setVariableImperiale(imperial);
        _positionY.setVariableImperiale(imperial);

        return new String[][]{
                {"Largeur", _largeur.toString()},
                {"Hauteur", _hauteur.toString()},
                {"Position X", _positionX.toString()},
                {"Position Y", _positionY.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Ouverture latérale";
    }
}
