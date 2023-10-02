package ca.ulaval.glo2004.domaine.planRoulotte.Factory;

public class DTOPointControle extends DTOElement{
    private final boolean _selection;

    public DTOPointControle(boolean selection) {
        _selection = selection;
    }

    public boolean isSelected() {return _selection;}


    @Override
    public String[][] getListeVariables(boolean imperial) {
        return new String[0][];
    }

    @Override
    public String getNom() {
        return "Point de contrôle";
    }
}
