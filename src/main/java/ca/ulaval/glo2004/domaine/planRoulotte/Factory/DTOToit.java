package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

public final class DTOToit extends DTOElement {
    private final Mesure _epaisseurDado;
    private final boolean _selection;

    public DTOToit(Mesure epaisseurDado, boolean selection) {
        _epaisseurDado = epaisseurDado;
        _selection = selection;
    }

    public Mesure getEpaisseurDado() {return _epaisseurDado;}
    public boolean isSelection() {return _selection;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _epaisseurDado.setVariableImperiale(imperial);

        return new String[][]{
                {"Épaisseur dado", _epaisseurDado.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Toit";
    }
}
