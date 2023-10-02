package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ProfilElliptique extends Profil implements Serializable {

    private List<Ellipse> ellipseListe;


    public ProfilElliptique(List<Ellipse> ellipseListe){
        this.ellipseListe = ellipseListe;
    }


    public List<Ellipse> getProfilElliptique() {return this.ellipseListe;}

}