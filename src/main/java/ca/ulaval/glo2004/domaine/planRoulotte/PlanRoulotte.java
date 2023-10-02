package ca.ulaval.glo2004.domaine.planRoulotte;

import ca.ulaval.glo2004.domaine.planRoulotte.Factory.*;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.*;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlanRoulotte implements Serializable {
    private Mesure longueur;
    private Mesure hauteur;
    private boolean estImperial;
    private Plancher plancher;
    private MurSeparateur murSeparateur = null;
    private Toit toit;
    private PoutreArriere poutreArriere;
    private List<AideAuDesign> listeAideAuDesign = new ArrayList<>();
    private List<OuvertureLaterale> listeOuverturesLaterales = new ArrayList<>();
    private Hayon hayon;
    private Murs murs;
    private PROFIL_TYPE profilType;
    private RessortAGaz ressortAGaz;
    private Vector<ElementSelectionnable> elementsSelectionnables = new Vector<>();
    private List<Ellipse> ellipseListe = new ArrayList<>();
    private List<PointControle> ptsControleListe = new ArrayList<>();
    private String cheminEnregistrement = null;
    private Grille grille;
    private boolean isExportJPG;
    public enum PROFIL_TYPE {
        ELLIPTIQUE, BEZIER
    }

    public PlanRoulotte(boolean estEllipse) {
        longueur = new Mesure(96, 0, 0); // 8'
        hauteur = new Mesure(48, 0, 0); // 4'
        grille = new Grille();
        this.estImperial = true; // On avec des mesures impériales

        if (estEllipse) {
            ProfilElliptique elliptique = new ProfilElliptique(ellipseListe);
            setProfilElliptique();
        } else {
            ProfilBezier bezier = new ProfilBezier();
            setProfilBezier();
        }

    }

    public List<Ellipse> getEllipseListe() {
        for (Ellipse ellipse : ellipseListe){
            if (!ellipse.dispositionAJour())
                ellipse.calculDisposition();
        }
        return ellipseListe;}


    public void setExportType(boolean isJPG){
        isExportJPG = isJPG;
    }
    public boolean isExportJPG(){
        return isExportJPG;
    }
    public void setProfilBezier() {
        this.profilType = PROFIL_TYPE.BEZIER;
        this.murs = new Murs(this);
        elementsSelectionnables.add(this.murs.getPanneauInterieur());
        Plancher plancher = new Plancher(new Mesure(0,0,0), new Mesure(14,0, 0), new Mesure(0, 5, 4), this);
        this.plancher = plancher;
        elementsSelectionnables.add(plancher);

        PointControle pt1 = new PointControle(1, plancher, murs.getPanneauInterieur());
        PointControle pt2 = new PointControle(2, plancher, murs.getPanneauInterieur());
        PointControle pt3 = new PointControle(3, plancher, murs.getPanneauInterieur());
        PointControle pt4 = new PointControle(4,plancher, murs.getPanneauInterieur());

        elementsSelectionnables.add(pt1);
        elementsSelectionnables.add(pt2);
        elementsSelectionnables.add(pt3);
        elementsSelectionnables.add(pt4);
        ptsControleListe.add(pt1);
        ptsControleListe.add(pt2);
        ptsControleListe.add(pt3);
        ptsControleListe.add(pt4);
    }

    public void setProfilElliptique(){
        this.profilType = PROFIL_TYPE.ELLIPTIQUE;
        // Gestion des ellipes
        Ellipse ellipse1 = new Ellipse(new Point2D.Double(0,0),new Mesure(15,0,0), new Mesure(20,0,0),1, UUID.randomUUID(), this);
        Ellipse ellipse2 = new Ellipse(new Point2D.Double(longueur.getMillimetres(),0),new Mesure(25,0,0), new Mesure(12,0,0),2, UUID.randomUUID(), this);
        Ellipse ellipse3 = new Ellipse(new Point2D.Double(longueur.getMillimetres(),hauteur.getMillimetres()),new Mesure(12,0,0), new Mesure(17,0,0),3, UUID.randomUUID(), this);
        Ellipse ellipse4 = new Ellipse(new Point2D.Double(0,hauteur.getMillimetres()),new Mesure(15,0,0), new Mesure(15,0,0),4, UUID.randomUUID(), this);
        ellipseListe.add(ellipse1);
        ellipseListe.add(ellipse2);
        ellipseListe.add(ellipse3);
        ellipseListe.add(ellipse4);
        Ellipse.parentStatic = this;
        this.murs = new Murs(ellipseListe, this);
        this.elementsSelectionnables.add(this.murs.getPanneauInterieur());
        this.elementsSelectionnables.add(ellipse1);
        this.elementsSelectionnables.add(ellipse2);
        this.elementsSelectionnables.add(ellipse3);
        this.elementsSelectionnables.add(ellipse4);

        // Gestion du plancher
        Plancher plancher = new Plancher(new Mesure(15,0,0), new Mesure(22,0, 0), new Mesure(0, 5, 4), this);
        this.plancher = plancher;
        elementsSelectionnables.add(plancher);

        // Gestion de la poutre arrière
        PoutreArriere poutreArriere = new PoutreArriere(new Mesure(6,0,0), new Mesure(2, 0,0), new Mesure(20,0, 0), new Point(508, 0), this);
        this.poutreArriere = poutreArriere;
        elementsSelectionnables.add(poutreArriere);

        // Gestion du hayon
        Hayon hayon = new Hayon(this);
        this.hayon = hayon;
        elementsSelectionnables.add(hayon);

        // Gestion du toit
        Toit toit = new Toit(this);
        this.toit = toit;
        elementsSelectionnables.add(toit);

        // Gestion du mur séparateur
        MurSeparateur murSeparateur = new MurSeparateur(this);
        this.murSeparateur = murSeparateur;
        elementsSelectionnables.add(murSeparateur);

        // Gestion du ressort
        RessortAGaz ressortAGaz = new RessortAGaz(this);
        this.ressortAGaz = ressortAGaz;
        elementsSelectionnables.add(ressortAGaz);

        invalideLaDisposition();
    }

    public void miseAJourPositionElementSelectionne(Point2D.Double delta){
        for (ElementSelectionnable element : this.elementsSelectionnables) {
            if (element.isSelected()) {
                element.translate(delta);
                this.invalideLaDisposition();
            }
        }
    }

    public void invalideLaDisposition(){
        for (ElementSelectionnable element : this.elementsSelectionnables) {
            element.invalideLaDisposition();
        }
    }

    public void exportePlanJPG() {}
    public void exportePlanSVG() {}

    public void undo() {}
    public void redo() {}

    public void supprimeElementSelectionne(ElementSelectionnable element) {
        ElementSelectionnable.TypeElement type = element.getType();
        if (type == ElementSelectionnable.TypeElement.AIDEAUDESIGN) {
            listeAideAuDesign.remove(element);
            elementsSelectionnables.remove(element);
        }
        else if (type == ElementSelectionnable.TypeElement.OUVERTURELATERALE) {
            listeOuverturesLaterales.remove(element);
            elementsSelectionnables.remove(element);
        }
        else if (type == ElementSelectionnable.TypeElement.MURSEPARATEUR) {
            murSeparateur = null;
            elementsSelectionnables.remove(element);
        }
        invalideLaDisposition();
    }

    public void supprimeEmelementsApresPointeur(int undoRedoPointer) {}
    public void ajouteAideAuDesign(Point2D.Double pointInitial, Mesure largeur, Mesure hauteur) {
        for (ElementSelectionnable element : elementsSelectionnables) {
            element.setNonSelectionne();
        }
        // check la grosseur du panneau pour voir si ça fit
        Mesure largeurPanneau = murs.getPanneauInterieur().getLargeurBrute();
        Mesure hauteurPanneau = murs.getPanneauInterieur().getHauteurBrute();

        Mesure largeurPasse = new Mesure(largeur.getPoucesEntier(),largeur.getPoucesFractionsNumerateur(),largeur.getPoucesFractionsDenominateur());
        Mesure hauteurPasse = new Mesure(hauteur.getPoucesEntier(),hauteur.getPoucesFractionsNumerateur(),hauteur.getPoucesFractionsDenominateur());

        if (pointInitial.getX() < 0) { pointInitial.setLocation(0, pointInitial.getY());}
        if (pointInitial.getY() < 0) { pointInitial.setLocation(pointInitial.getX(), 0);}

        // TODO: gérer la création des aides au design dans les courbes des ellipses en fonction des points d'intersection
        Polygon panneauPoly = murs.getPanneauInterieur().getPolygon();
        Rectangle tempRect = new Rectangle((int)pointInitial.getX(), (int)pointInitial.getY(), largeur.getMillimetres(), hauteur.getMillimetres());


        if (pointInitial.getX() + largeur.getMillimetres() > largeurPanneau.getMillimetres()) {
            largeurPasse.setMetrique(largeurPanneau.getMillimetres() - (int)pointInitial.getX() - 50);
        }
        if(pointInitial.getY() + hauteur.getMillimetres() > hauteurPanneau.getMillimetres()) {
            hauteurPasse.setMetrique(hauteurPanneau.getMillimetres() - (int) pointInitial.getY());
        }

        AideAuDesign newAide = new AideAuDesign(largeurPasse, hauteurPasse, pointInitial, murs.getPanneauInterieur());
        listeAideAuDesign.add(newAide);
        elementsSelectionnables.add(newAide);
        //invalideLaDisposition();

    }

    public void ajouteMurSeparateur() { // a retravailler
        if(murSeparateur == null){

            Mesure distancePoutre = new Mesure(0,0,0);

            Mesure distanceDuPlancher = new Mesure(0,0,0);

            int arcXMax = Ellipse.getPtCoinSupGaucheFin().x;
            int positionMurDebut = poutreArriere.getPosition().x + poutreArriere.getLargeur().getMillimetres(); // a retravailler non exacte

            if(positionMurDebut < arcXMax){
                distancePoutre.setMetrique(arcXMax - positionMurDebut);
            }
            else {
                distancePoutre.setMetrique(0);
            }

            murSeparateur = new MurSeparateur(new Mesure(2,0,0), new Mesure(0,0,0), distancePoutre, this);
            elementsSelectionnables.add(murSeparateur);
            invalideLaDisposition();
        }
    }

    public void ajouteOuvertureLaterale(Point2D.Double pointInitial, Mesure largeur, Mesure hauteur) {
        for (ElementSelectionnable element : elementsSelectionnables) {
            element.setNonSelectionne();
        }
        // check la grosseur du panneau pour voir si ça fit
        Mesure largeurPanneau = murs.getPanneauInterieur().getLargeurBrute();
        Mesure hauteurPanneau = murs.getPanneauInterieur().getHauteurBrute();

        Mesure largeurPasse = new Mesure(largeur.getPoucesEntier(),largeur.getPoucesFractionsNumerateur(),largeur.getPoucesFractionsDenominateur());
        Mesure hauteurPasse = new Mesure(hauteur.getPoucesEntier(),hauteur.getPoucesFractionsNumerateur(),hauteur.getPoucesFractionsDenominateur());

        if (pointInitial.getX() < 0) { pointInitial.setLocation(0, pointInitial.getY());}
        if (pointInitial.getY() < 0) { pointInitial.setLocation(pointInitial.getX(), 0);}

        // TODO: gérer la création des aides au design dans les courbes des ellipses en fonction des points d'intersection
        Polygon panneauPoly = murs.getPanneauInterieur().getPolygon();
        Rectangle tempRect = new Rectangle((int)pointInitial.getX(), (int)pointInitial.getY(), largeur.getMillimetres(), hauteur.getMillimetres());


        if (pointInitial.getX() + largeur.getMillimetres() > largeurPanneau.getMillimetres()) {
            largeurPasse.setMetrique(largeurPanneau.getMillimetres() - (int)pointInitial.getX() - 50);
        }
        if(pointInitial.getY() + hauteur.getMillimetres() > hauteurPanneau.getMillimetres()) {
            hauteurPasse.setMetrique(hauteurPanneau.getMillimetres() - (int) pointInitial.getY());
        }

        OuvertureLaterale ouvLat = new OuvertureLaterale(largeurPasse, hauteurPasse, pointInitial, murs.getPanneauInterieur());
        listeOuverturesLaterales.add(ouvLat);
        elementsSelectionnables.add(ouvLat);
    }
    public void configureRessortAGaz() {}

    public Mesure getLongueur() { return longueur;}
    public Mesure getHauteur() {return hauteur;}

    public void setEstImperial(boolean EstMetrique) {
        estImperial = EstMetrique;}

    public boolean getEstImperial() {return this.estImperial;}

    public Vector<ElementSelectionnable> getElementsSelectionnables() {return elementsSelectionnables;}

    public Vector<ElementSelectionnable> getElementsSelectionnablesExportable(){
        Vector<ElementSelectionnable> elements = new Vector<ElementSelectionnable>();
        Plancher newPlancher = new Plancher(new Mesure(0,0,0),
                new Mesure(0,0,0), getDTOPlancher().getEpaisseur(), this);

        for (ElementSelectionnable elem: elementsSelectionnables) {
            if (!(elem instanceof Ellipse) && !(elem instanceof RessortAGaz && !(elem instanceof Plancher)))
                elements.add(elem);
        }
        elements.add(newPlancher);
        return elements;
    }

    public Polygon getContour(){
        Polygon retour = new Polygon();

        PlanRoulotte.objectToPolygon(retour, Ellipse.getArc1().getPathIterator(null));
        PlanRoulotte.objectToPolygon(retour, Ellipse.getArc2().getPathIterator(null));
        PlanRoulotte.objectToPolygon(retour, Ellipse.getArc3().getPathIterator(null));
        PlanRoulotte.objectToPolygon(retour, Ellipse.getArc4().getPathIterator(null));

        return retour;
    }

    public DTOPanneau getDTOPanneau(){return murs.getDTOPanneau();}

    int nbElementSelectionne(){
        int i = 0;
        for (ElementSelectionnable element : this.elementsSelectionnables){
            if(element.isSelected()){
                i++;
            }
        }
        return i;
    }

    public void changeDimPanneau(Mesure hauteur, Mesure largeur){
        murs.getPanneauInterieur().setHauteurBrute(hauteur);
        murs.getPanneauInterieur().setLargeurBrute(largeur);
        this.hauteur = hauteur;
        this.longueur = largeur;
        for(Ellipse ellipse : this.ellipseListe) {
            ellipse.setRedimensionPointInitial(hauteur.getMillimetres(), largeur.getMillimetres());
        }
        invalideLaDisposition();
    }

    public void changeDimPlancher(Mesure epaisseur, Mesure margeAvant, Mesure margeArriere){
        calculProfilElliptique();
        Mesure largeurPanneau = this.getDTOPanneau().getLargeurBrute();
        if (profilType == PROFIL_TYPE.ELLIPTIQUE) {
            int xMax = Ellipse.getPtCoinInfDroitFin().x;
            int xMin = Ellipse.getPtCoinInfGaucheDebut().x;

            if (margeArriere.getMillimetres() >= xMin && ((largeurPanneau.getMillimetres() - margeAvant.getMillimetres()) <= xMax)) {
                plancher.setEpaisseur(epaisseur);
                plancher.setMargeAvant(margeAvant);
                plancher.setMargeArriere(margeArriere);
                invalideLaDisposition();
            }
        } else {
            plancher.setEpaisseur(epaisseur);
            plancher.setMargeAvant(margeAvant);
            plancher.setMargeArriere(margeArriere);
            invalideLaDisposition();
        }

    }
    //TODO: a enlever
    public void changeDimEllipse(Mesure hauteur, Mesure largeur, Mesure x, Mesure y, UUID id){
        Ellipse el = ellipseListe.stream().filter(f -> f.getId() == id).collect(Collectors.toList()).get(0);
        el.changeDims(x,y,largeur,hauteur);
    }

    public void changeDimPoutreArriere(Mesure hauteur, Mesure largeur, Mesure positionRelative){

        this.calculProfilElliptique();
        Polygon arcSupGauche = Ellipse.getArc1();
        Polygon arcSupDroit = Ellipse.getArc2();
        int arc1XMin = arcSupGauche.xpoints[0];
        int arc2XMin = arcSupDroit.xpoints[0];

        for(int i = 0 ; i < arcSupGauche.npoints; i++){
            if(arcSupGauche.xpoints[i] < arc1XMin){
                arc1XMin = arcSupGauche.xpoints[i];
            }
        }
        for(int i = 0 ; i < arcSupDroit.npoints; i++) {
            if (arcSupDroit.xpoints[i] < arc2XMin) {
                arc2XMin = arcSupDroit.xpoints[i];
            }
        }
        if(arc1XMin <= positionRelative.getMillimetres() && (positionRelative.getMillimetres() + largeur.getMillimetres()) <= arc2XMin){
            poutreArriere.setPositionRelative(positionRelative);
            poutreArriere.setHauteur(hauteur);
            poutreArriere.setLargeur(largeur);
            invalideLaDisposition();
        }

    }

    public void changeDimMurSeparateur(Mesure epaisseurDado, Mesure distanceDuPlancher, Mesure distancePoutre) {


        int arcXMax = Ellipse.getPtCoinSupGaucheFin().x;

        // Trouver la position maximum du mur séparateur
        Polygon copiePoutreArriere = new Polygon(this.getPoutreArriere().getPolygon().xpoints, this.getPoutreArriere().getPolygon().ypoints, this.getPoutreArriere().getPolygon().npoints);

        int posXMaxPoutreArr = -1;

        for (int i=0; i<copiePoutreArriere.npoints; i++) {
            if (copiePoutreArriere.xpoints[i] > posXMaxPoutreArr) {posXMaxPoutreArr = copiePoutreArriere.xpoints[i];}
        }

        if(posXMaxPoutreArr + distancePoutre.getMillimetres() >= arcXMax){
            this.murSeparateur.setDistancePoutreArriere(distancePoutre);
            this.murSeparateur.setDistanceDuPlancher(distanceDuPlancher);
            this.murSeparateur.setEpaisseurDado(epaisseurDado);
            invalideLaDisposition();
        }
    }

    public void changeDimAideAuDesign(Mesure largeur, Mesure hauteur, Mesure positionX, Mesure positionY, AideAuDesign objetSelection) {
        objetSelection.setLargeur(largeur);
        objetSelection.setHauteur(hauteur);
        objetSelection.setPositionX(positionX);
        objetSelection.setPositionY(positionY);
    }

    public void changeDimOuvLat(Mesure largeur, Mesure hauteur, Mesure positionX, Mesure positionY, OuvertureLaterale objetSelection) {
        objetSelection.setLargeur(largeur);
        objetSelection.setHauteur(hauteur);
        objetSelection.setPositionX(positionX);
        objetSelection.setPositionY(positionY);
    }

    public Vector<ElementSelectionnable> getElementSelectionnables() {return elementsSelectionnables;}

    public void alternerSelectionPourCoordonnee(int x, int y) {
        Point point = new Point(x,y);
        for (ElementSelectionnable element : this.elementsSelectionnables) {
            if (element.estVisible()) {
                if (element.contient(point) && (nbElementSelectionne() < 1)){
                    element.switchSelection();
                }
                else {
                    element.setNonSelectionne();
                }
            }
        }
    }

    public ElementSelectionnable.TypeElement whatElementIsSelected() {
        for (ElementSelectionnable element : this.elementsSelectionnables) {
            if (element.isSelected()) {
                return element.getType();
            }
        }
        return ElementSelectionnable.TypeElement.NOTHING;
    }

    public ElementSelectionnable whatObjectIsSelected() {
        for (ElementSelectionnable element : this.elementsSelectionnables) {
            if (element.isSelected()) {
                return element;
            }
        }
        return null;
    }

    public static void objectToPolygon(Polygon polygoneRetour, PathIterator iterator) {
        float[] coordonnees = new float[6];
        while (!iterator.isDone()) {
            int temoin = iterator.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            polygoneRetour.addPoint(x, y);
            iterator.next();
        }
    }

    public DTOPlancher getDTOPlancher() {
        return plancher.getElementDTO();
    }

    public DTOPoutreArriere getDTOPoutreArriere() {
        return poutreArriere.getElementDTO();
    }

    public void changeDimHayon(Mesure epaisseur, Mesure distanceDePoutre, Mesure distanceDuPlancher, Mesure traitDeScie, Mesure rayonCourbure, Double poids) {

        if(rayonCourbure.getMillimetres() < epaisseur.getMillimetres()){
            rayonCourbure.setMetrique(epaisseur.getMillimetres());
        }
        hayon.setEpaisseur(epaisseur);
        hayon.setDistanceDePoutre(distanceDePoutre);
        hayon.setDistanceDuPlancher(distanceDuPlancher);
        hayon.setTraitDeScie(traitDeScie);
        hayon.setPoids(poids);
        hayon.setRayonCourbure(rayonCourbure);
        invalideLaDisposition();
    }

    public void changeDimToit(Mesure epaisseurDado) {
        toit.setEpaisseurDado(epaisseurDado);
        invalideLaDisposition();
    }

    public DTOHayon getDTOHayon() {
        return hayon.getElementDTO();
    }

    public DTOToit getDTOToit() {
        return toit.getElementDTO();
    }

    public DTOMurSeparateur getDTOMurSeparateur() {
        return murSeparateur.getElementDTO();
    }

    public static Point tangenteCercleDroite(Point p1, Point p2, Ellipse2D.Double cercle) {
        double deltaX = p2.x-p1.x;
        double deltaY = p2.y-p1.y;
        double deltaTangente = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        double moitieDeltaX = deltaX/2.0;
        double moitieDeltaY = deltaY/2.0;
        Point p3 = new Point(p1.x+(int)moitieDeltaX, p1.y+(int)moitieDeltaY);

        double rayonCercle = cercle.width/2;

        return new Point((int)(p3.x+rayonCercle*(p1.y-p2.y)/deltaTangente), (int)(p3.y-rayonCercle*(p1.x-p2.x)/deltaTangente));
    }

    public static boolean verifieSiPointSurCourbe(Point pt, Ellipse2D.Double ellipse, double tolerance) {
        FlatteningPathIterator iterator = new FlatteningPathIterator(ellipse.getPathIterator(null), 0.1);
        Ellipse2D.Double cercleTolerance= new Ellipse2D.Double(pt.x-tolerance/2, pt.y-tolerance/2, tolerance*2, tolerance*2);

        float[] coordonnees = new float[6];
        while (!iterator.isDone()) {
            int temoin = iterator.currentSegment(coordonnees);
            int x = (int) coordonnees[0];
            int y = (int) coordonnees[1];
            if (cercleTolerance.contains(new Point(x,y))) {return true;}
            iterator.next();
        }
        return false;
    }

    public static Point trouvePointSurCourbe(Point pt1, Point pt2, PathIterator iterator) {// TODO : À ajouter au UML
        //FlatteningPathIterator iterator = new FlatteningPathIterator(pathIteratorCourbe);

        float[] coordonnees = new float[6];
        int temoinAvant = iterator.currentSegment(coordonnees);
        Point ptAvant = new Point((int) coordonnees[0], (int) coordonnees[1]);
        iterator.next();
        while (!iterator.isDone()) {
            int temoin = iterator.currentSegment(coordonnees);
            int x = (int) coordonnees[0];
            int y = (int) coordonnees[1];

            Point ptIntersection = PlanRoulotteControleur.pointIntersection(pt1, pt2, ptAvant, new Point(x,y));
            if (ptIntersection != null) {
                return ptIntersection;
            }
            ptAvant.setLocation(x, y);
            iterator.next();
        }
        return null;
    }


    public Hayon getHayon() {
        hayon.calculDisposition();
        return this.hayon;
    }

    public PoutreArriere getPoutreArriere() {
        if (!poutreArriere.dispositionAJour()){
            poutreArriere.calculDisposition();
        }
        return this.poutreArriere;
    }

    //TODO: Ajouter uml
    public String getCheminEnregistrement() {
        return cheminEnregistrement;
    }

    public void setCheminEnregistrement(String chemin) {
        this.cheminEnregistrement = chemin;
    }

    public Grille getGrille() {
        return grille;
    }

    public void calculProfilElliptique(){
        murs.getPanneauInterieur().calculDispositionProfilElliptique(getEllipseListe());
    }

    public void setEtatGrille() {
        grille.setActive(!grille.isActive());
    }

    public void setEspacementGrille(Mesure mesure) {
        grille.setEspacement(mesure);
    }

    public Mesure getEspacementGrille() {
        return grille.getEspacement();
    }

    public void setElementVisibilite(ElementSelectionnable.TypeElement type) {
        switch (type) {
            case ELLIPSE:
                if (!ellipseListe.isEmpty()) {
                    for (Ellipse e: ellipseListe) {
                        e.setVisibilite();
                    }
                }
                break;
            case AIDEAUDESIGN:
                if (!listeAideAuDesign.isEmpty()) {
                    for (AideAuDesign a: listeAideAuDesign) {
                        a.setVisibilite();
                    }
                }
                break;
            case OUVERTURELATERALE:
                if (!listeOuverturesLaterales.isEmpty()) {
                    for(OuvertureLaterale o: listeOuverturesLaterales) {
                        o.setVisibilite();
                    }
                }
                break;
            case TOIT:
                toit.setVisibilite();
                break;
            case HAYON:
                hayon.setVisibilite();
                break;
            case PLANCHER:
                plancher.setVisibilite();
                break;
            case POUTREARRIERE:
                poutreArriere.setVisibilite();
                break;
            case RESSORTAGAZ:
                ressortAGaz.setVisibilite();
                break;
            case MURSEPARATEUR:
                if (murSeparateur != null) {
                    murSeparateur.setVisibilite();
                }
        }
    }

    public PROFIL_TYPE getProfilType() {
        return profilType;
    }

    public List<PointControle> getPtsControleListe() {
        return ptsControleListe;
    }

    public Polygon getPolyPanneauBrut() {
        return this.murs.getPanneauInterieur().getPolygonPanneauBrut();
    }
}
