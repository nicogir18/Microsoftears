package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

public final class DTOHayon extends DTOElement {
    private final Mesure _distanceDePoutre;
    private final Mesure _epaisseur;
    private final Mesure _distanceDuPlancher;
    private final Mesure _traitDeScie;
    private final Double _poids;
    private final Mesure _rayonCourbure;
    private final boolean _selection;


    public DTOHayon(Mesure distanceDePoutre, Mesure epaisseur, Mesure distanceDuPlancher, Mesure traitDeScie,
                    Double poids, Mesure rayonCourbure, boolean selection){
        _distanceDePoutre = distanceDePoutre;
        _epaisseur = epaisseur;
        _distanceDuPlancher = distanceDuPlancher;
        _traitDeScie = traitDeScie;
        _poids = poids;
        _rayonCourbure = rayonCourbure;
        _selection = selection;

    }

    public Mesure getDistanceDePoutre() {return _distanceDePoutre;}
    public Mesure getDistanceDuPlancher() {return _distanceDuPlancher;}
    public Mesure getEpaisseur() {return _epaisseur;}
    public Double getPoids() {return _poids;}
    public Mesure getRayonCourbure() {return _rayonCourbure;}
    public Mesure getTraitDeScie() {return _traitDeScie;}
    public boolean isSelected(){return _selection;}

    @Override
    public String[][] getListeVariables(boolean imperial) {
        _epaisseur.setVariableImperiale(imperial);
        _distanceDePoutre.setVariableImperiale(imperial);
        _distanceDuPlancher.setVariableImperiale(imperial);
        _traitDeScie.setVariableImperiale(imperial);
        _rayonCourbure.setVariableImperiale(imperial);

        return new String[][]{
                {"Épaisseur", _epaisseur.toString()},
                {"Distance de poutre", _distanceDePoutre.toString()},
                {"Distance du plancher", _distanceDuPlancher.toString()},
                {"Trait de scie", _traitDeScie.toString()},
                {"Poids", _poids.toString()},
                {"Rayon de courbure", _rayonCourbure.toString()}
        };
    }

    @Override
    public String getNom() {
        return "Hayon";
    }
}
