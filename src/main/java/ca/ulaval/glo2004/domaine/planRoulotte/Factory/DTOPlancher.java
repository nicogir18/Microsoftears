package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

public final class DTOPlancher extends DTOElement {
    private final Mesure _margeAvant;
    private final Mesure _margeArriere;
    private final Mesure _epaisseur;
    private final boolean _selection;

    public DTOPlancher(Mesure margeAvant, Mesure margeArriere, Mesure epaisseur, boolean selection) {
        _margeAvant = margeAvant;
        _margeArriere = margeArriere;
        _epaisseur = epaisseur;
        _selection = selection;
    }

    public Mesure getMargeAvant(){return _margeAvant;}
    public Mesure getMargeArriere(){return _margeArriere;}
    public Mesure getEpaisseur(){return _epaisseur;}
    public boolean isSelected(){return _selection;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _epaisseur.setVariableImperiale(imperial);
        _margeAvant.setVariableImperiale(imperial);
        _margeArriere.setVariableImperiale(imperial);

        return new String[][]{
                {"Épaisseur", _epaisseur.toString()},
                {"Marge avant", _margeAvant.toString()},
                {"Marge arrière", _margeArriere.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Panneau";
    }
}
