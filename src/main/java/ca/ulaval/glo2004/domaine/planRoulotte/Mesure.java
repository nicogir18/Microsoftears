package ca.ulaval.glo2004.domaine.planRoulotte;

import java.io.Serializable;

import static java.lang.Math.floor;
import static java.lang.Math.round;

public class Mesure implements Serializable {
    private int poucesEntier;
    private int poucesFractionsDenominateur;
    private int poucesFractionsNumerateur;

    private int millimetres;

    private boolean estImperial; // On va être en pouces initialement

    public Mesure(int poucesEntier, int poucesFractionsNumerateur, int poucesFractionsDenominateur){
        this.estImperial = true;
        this.poucesEntier = poucesEntier;
        this.poucesFractionsNumerateur = poucesFractionsNumerateur;
        this.poucesFractionsDenominateur = poucesFractionsDenominateur;

        // Pour convertir la mesure en pouces -> mm
        float poucesDecimal;
        if (poucesFractionsNumerateur == 0 || poucesFractionsDenominateur == 0) {
            poucesDecimal = this.poucesEntier;
        }
        else {
            poucesDecimal = (float) this.poucesEntier + ((float) this.poucesFractionsNumerateur / (float) this.poucesFractionsDenominateur);
        }

        this.millimetres = round(poucesDecimal * 25.4f);
    }

    public void setVariableImperiale(boolean imperiale) {
        this.estImperial = imperiale;
    }

    public void setImperiale(int poucesEntier, int poucesFractionsNumerateur, int poucesFractionsDenominateur) {
        this.estImperial = true;
        this.poucesEntier = poucesEntier;
        this.poucesFractionsNumerateur = poucesFractionsNumerateur;
        this.poucesFractionsDenominateur = poucesFractionsDenominateur;

        // Pour convertir la mesure en pouces -> mm
        double poucesDecimal = (double) this.poucesEntier + ((double) this.poucesFractionsNumerateur / (double) this.poucesFractionsDenominateur);

        this.millimetres = (int) round(poucesDecimal * 25.4d);
    }

    public void setMetrique(int millimetres) {
        this.estImperial = false;
        this.millimetres = millimetres;

        // Pour convertir la mesure en mm -> pouces
        double convertion = millimetres / 25.4d;
        double partieDecimale = convertion % 1;
        this.poucesEntier = (int) (convertion - partieDecimale);

        partieDecimale = floor(partieDecimale * 64); // pour avoir la fraction sur /64
        int b = 1;
        if (partieDecimale != 0) {
            while (partieDecimale % 2 == 0) {
                partieDecimale /= 2;
                b++;
            }
        }

        this.poucesFractionsNumerateur = (int) partieDecimale;
        this.poucesFractionsDenominateur = (int) (64 / Math.pow(2, (b-1)));
    }

    public int getPoucesEntier() {
        return poucesEntier;
    }

    public int getPoucesFractionsDenominateur() {
        return poucesFractionsDenominateur;
    }

    public int getPoucesFractionsNumerateur() {
        return poucesFractionsNumerateur;
    }

    public int getMillimetres() {
        return millimetres;
    }

    public boolean getEstMetrique() {
        return estImperial;
    }

    @Override
    public String toString(){
        if (estImperial){
            if (getPoucesFractionsNumerateur() == 0 && getPoucesFractionsDenominateur() == 0)
                return Integer.toString(getPoucesEntier());

            return String.format("%s %s/%s", getPoucesEntier(),
                    getPoucesFractionsNumerateur(),
                    getPoucesFractionsDenominateur());
        }
        return Integer.toString(getMillimetres());
    }
}
