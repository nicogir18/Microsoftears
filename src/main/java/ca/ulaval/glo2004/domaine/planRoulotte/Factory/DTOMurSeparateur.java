package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

public final class DTOMurSeparateur extends DTOElement {
    private final Mesure _epaisseurDado;
    private final Mesure _distanceDuPlancher;
    private final Mesure _distancePoutreArriere;
    private final boolean _selection;

    public DTOMurSeparateur(Mesure epaisseurDado, Mesure distanceDuPlancher, Mesure distancePoutreArriere,
                            boolean selection){
        _epaisseurDado = epaisseurDado;
        _distanceDuPlancher = distanceDuPlancher;
        _distancePoutreArriere = distancePoutreArriere;
        _selection = selection;
    }

    public Mesure getEpaisseurDado() {
        return _epaisseurDado;
    }

    public Mesure getDistanceDuPlancher() {
        return _distanceDuPlancher;
    }

    public Mesure getDistancePoutreArriere() {
        return _distancePoutreArriere;
    }

    public boolean isSelected() {
        return _selection;
    }

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _epaisseurDado.setVariableImperiale(imperial);
        _distanceDuPlancher.setVariableImperiale(imperial);
        _distancePoutreArriere.setVariableImperiale(imperial);

        return new String[][]{
                {"Épaisseur du dado", _epaisseurDado.toString()},
                {"Distance du plancher", _distanceDuPlancher.toString()},
                {"Distance poutre arrière", _distancePoutreArriere.toString()},
        };
    }

    @Override
    public String getNom() {
        return "Mur séparateur";
    }
}
