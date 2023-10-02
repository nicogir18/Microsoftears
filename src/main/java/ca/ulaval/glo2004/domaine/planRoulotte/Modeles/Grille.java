package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import java.io.Serializable;

public class Grille implements Serializable {
    private Mesure espacement;

    private boolean isActive;

    public Grille() {
        isActive = false;
        espacement = new Mesure(2,0,0);
    }

    public Mesure getEspacement() {
        return espacement;
    }

    public void setEspacement(Mesure espacement) {
        this.espacement = espacement;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
