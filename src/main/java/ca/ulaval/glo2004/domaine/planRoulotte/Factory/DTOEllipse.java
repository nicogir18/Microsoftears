package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.awt.geom.Ellipse2D;
import java.util.UUID;

public final class DTOEllipse extends DTOElement {
    private final Ellipse2D _ellipse;
    private final Mesure _largeur;
    private final Mesure _hauteur;
    private final Mesure _x;
    private final Mesure _y;
    private final UUID _id;

    public DTOEllipse(Ellipse2D ellipse, Mesure x, Mesure y, Mesure largeur, Mesure hauteur) {
        this(ellipse,x,y,largeur,hauteur, UUID.randomUUID());
    }

    public DTOEllipse(Ellipse2D ellipse, Mesure x, Mesure y, Mesure largeur, Mesure hauteur, UUID id) {
        _ellipse = ellipse;
        _largeur = largeur;
        _hauteur = hauteur;
        _x = x;
        _y = y;
        _id = id;
    }

    public Ellipse2D getEllipse() {return _ellipse;}

    public Mesure getLargeur() {
        return _largeur;
    }

    public Mesure getHauteur() {
        return _hauteur;
    }

    public Mesure getX() {
        return _x;
    }

    public Mesure getY() {
        return _y;
    }

    public UUID getId() {
        return _id;
    }

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _hauteur.setVariableImperiale(imperial);
        _largeur.setVariableImperiale(imperial);
        _x.setVariableImperiale(imperial);
        _y.setVariableImperiale(imperial);

        return new String[][]{
                {"Hauteur", _hauteur.toString()},
                {"Largeur", _largeur.toString()},
                {"Position X", _x.toString()},
                {"Position Y", _y.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Ellipse";
    }
}
