package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;


import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOHayon;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Hayon extends ElementSelectionnable implements Serializable {

    private Mesure distanceDePoutre;
    private Mesure epaisseur;
    private Mesure distanceDuPlancher;
    private Mesure traitDeScie;
    private double poids;
    private Mesure rayonCourbure;
    private Polygon polygon;
    private Polygon polyTraitDeScie = new Polygon();
    private PlanRoulotte parent;
    private Point pointTopHayon;
    private Point pointBotHayon;
    private final int prioriteAffichage = 1;


    public Hayon(Mesure distanceDePoutre, Mesure epaisseur, Mesure distanceDuPlancher,
                 Mesure traitDeScie, double poids, Mesure rayonCourbure, PlanRoulotte parent){
        super(false, false);
        this.distanceDePoutre = distanceDePoutre;
        this.epaisseur = epaisseur;
        this.distanceDuPlancher = distanceDuPlancher;
        this.traitDeScie = traitDeScie;
        this.poids = poids;
        this.rayonCourbure = rayonCourbure;
        this.parent = parent;
        this.pointTopHayon = new Point();
        this.pointBotHayon = new Point();
        this.polygon = new Polygon(new int[]{0,epaisseur.getMillimetres(), epaisseur.getMillimetres(), 0}, new int[]{0,0,parent.getHauteur().getMillimetres(),parent.getHauteur().getMillimetres()}, 4);
    }

    public Hayon(PlanRoulotte parent){
        super(false, false);
        this.epaisseur = new Mesure(2, 0, 0);
        this.distanceDePoutre = new Mesure(0, 5, 16);
        this.rayonCourbure = new Mesure(2, 3, 8);
        this.distanceDuPlancher = new Mesure(0,3, 8);
        this.traitDeScie = new Mesure(0, 1, 16);
        this.poids = 50.0;
        this.parent = parent;
        this.pointTopHayon = new Point();
        this.pointBotHayon = new Point();
        this.polygon = new Polygon(new int[]{0,epaisseur.getMillimetres(), epaisseur.getMillimetres(), 0}, new int[]{0,0,parent.getHauteur().getMillimetres(),parent.getHauteur().getMillimetres()}, 4);
    }

    public void calculHayon() {
        Polygon polyRetour = new Polygon();

        // Création de copie des arcs 1 et 4 pour aider aux calculs du hayon
        Polygon arc1 = new Polygon(Ellipse.getArc1().xpoints, Ellipse.getArc1().ypoints, Ellipse.getArc1().npoints); // Sup gauche
        Polygon arc4 = new Polygon(Ellipse.getArc4().xpoints, Ellipse.getArc4().ypoints, Ellipse.getArc4().npoints); // Inf gauche

        // Contantes pour aider à la lecture
        int mmEpaisseur = this.epaisseur.getMillimetres();
        int mmTraitScie = this.traitDeScie.getMillimetres();
        double flatness = 0.1;

        // HAYON
        // =====
        // Courbe extérieur - DÉBUT
        // Du bas vers le haut
        Polygon hayonCourbeExt = new Polygon();

        // Arc 4 - Arc inférieur gauche
        Polygon arc4CourbeExt = new Polygon();

        // Détermination du point limite pour la création du hayon
        int xPtLimitePlancherHayon = parent.getDTOPlancher().getMargeArriere().getMillimetres()-this.distanceDuPlancher.getMillimetres();
        int yPtLimitePlancherHayon = parent.getDTOPanneau().getHauteurBrute().getMillimetres();

        Point ptLimitePlancherHayon = new Point(xPtLimitePlancherHayon, yPtLimitePlancherHayon);

        // On commence à ajouter l'arc en respectant la distance avec le plancher et le trait de scie
        hayonCourbeExt.addPoint(ptLimitePlancherHayon.x, ptLimitePlancherHayon.y);

        // On ajoute les points de l'arc 4 qui sont valides
        if (arc4.npoints>1) {
            for (int i = 0; i < arc4.npoints; i++) {
                if (arc4.xpoints[i] > xPtLimitePlancherHayon) {
                    continue;
                } // C'est que le point de l'arc est "dépassée" le plancher et la marge, etc.
                arc4CourbeExt.addPoint(arc4.xpoints[i], arc4.ypoints[i]);
            }
        }
        else {
            arc4CourbeExt.addPoint(0,parent.getDTOPanneau().getHauteurBrute().getMillimetres());
        }

        // Arc 1 - Arc supérieur gauche
        // C'est semblable. L'aspect qui change c'est que nous devons gérer par rapport à un point d'arrêt
        Polygon arc1CourbeExt = new Polygon();

        // Détermination du point limite pour la création du hayon
        int xPtLimitePoutreArriereHayon = (int) (parent.getDTOPoutreArriere().getPosition().getX()-this.distanceDePoutre.getMillimetres());
        int yPtLimitePoutreArriereHayon = (int) (parent.getDTOPoutreArriere().getPosition().getY());

        Point ptLimitePoutreArriereHayon = new Point(xPtLimitePoutreArriereHayon, yPtLimitePoutreArriereHayon);

        if (arc1.npoints>1) {
            for (int i = 0; i < arc1.npoints; i++) {
                if (arc1.xpoints[i] > xPtLimitePoutreArriereHayon || arc1.ypoints[i] < yPtLimitePoutreArriereHayon) {// C'est que le point de l'arc est "dépassée" le plancher et la marge, etc.
                    arc1CourbeExt.addPoint(xPtLimitePoutreArriereHayon, yPtLimitePoutreArriereHayon);
                    break;
                }
                arc1CourbeExt.addPoint(arc1.xpoints[i], arc1.ypoints[i]);
            }
        }
        else {
            arc1CourbeExt.addPoint(0,0);
        }

        // Il ne reste qu'à ajouter les deux courbes pour former la courbe extérieur du hayon
        PlanRoulotte.objectToPolygon(hayonCourbeExt, arc4CourbeExt.getPathIterator(null, flatness));
        PlanRoulotte.objectToPolygon(hayonCourbeExt, arc1CourbeExt.getPathIterator(null, flatness));

        // Ajout au polygone de retour
        PlanRoulotte.objectToPolygon(polyRetour, hayonCourbeExt.getPathIterator(null, flatness));

        // Je tag le point le plus bas et le plus haut du hayon. Ça va servir pour le calcul du ressort.
        this.pointBotHayon.setLocation(polyRetour.xpoints[0], polyRetour.ypoints[0]);
        this.pointTopHayon.setLocation(polyRetour.xpoints[polyRetour.npoints-1], polyRetour.ypoints[polyRetour.npoints-1]);
        // Courbe extérieur - FIN


        // Courbe intérieur - DÉBUT
        // Du bas vers le haut
        Polygon hayonCourbeInt = new Polygon();
        Polygon hayonCourbeIntInverse = new Polygon();

        // Je commence par créer la courbe classique (sans le cercle dans le top)
        AffineTransform at = new AffineTransform();
        double ratioX = (hayonCourbeExt.getBounds().getWidth() -(1.0*mmEpaisseur)) / hayonCourbeExt.getBounds().getWidth();
        double ratioY = (hayonCourbeExt.getBounds().getHeight()-(2.0*mmEpaisseur)) / hayonCourbeExt.getBounds().getHeight();

        at.setToScale(ratioX,ratioY);
        at.translate(Math.ceil((2.0-ratioX)*mmEpaisseur), Math.ceil((2.0-ratioY)*mmEpaisseur));

        PlanRoulotte.objectToPolygon(hayonCourbeInt, hayonCourbeExt.getPathIterator(at, flatness));

        // Je l'inverse car je vais en avoir besoin (plus facile pour la création du hayon et les calculs)
        for (int i=hayonCourbeInt.npoints-1; i>=0; i--) {
            hayonCourbeIntInverse.addPoint(hayonCourbeInt.xpoints[i], hayonCourbeInt.ypoints[i]);
        }

        // Gestion de la courbure pour la poutre arrière
        Polygon hayonCourbeIntInverseRetour = new Polygon();
        double distDroiteCalcul = Math.sqrt(2*Math.pow(mmEpaisseur,2));

        // Création de l'ellipse qui va servir à tracer le coin rond
        Ellipse2D.Double cercleHayonPoutreArriere = new Ellipse2D.Double(0,0,2*this.rayonCourbure.getMillimetres(), 2*this.rayonCourbure.getMillimetres());

        // Création de l'ellipse pour le calcul
        Ellipse2D.Double cercleHayonPoutreArriereCalcul = new Ellipse2D.Double(0,0, distDroiteCalcul*2.0, distDroiteCalcul*2.0);
        cercleHayonPoutreArriereCalcul.x = hayonCourbeExt.xpoints[hayonCourbeExt.npoints-1]-distDroiteCalcul;
        cercleHayonPoutreArriereCalcul.y = hayonCourbeExt.ypoints[hayonCourbeExt.npoints-1]-distDroiteCalcul;

        Point pt1 = new Point(polyRetour.xpoints[polyRetour.npoints-1], polyRetour.ypoints[polyRetour.npoints-1]);
        Point pt2 = new Point(arc1.xpoints[0], arc1.ypoints[0]);

        Point ptIntersection = null;

        for (int i=1; i<hayonCourbeIntInverse.npoints; i++) {
            Point ptAvant = new Point(hayonCourbeIntInverse.xpoints[i-1], hayonCourbeIntInverse.ypoints[i-1]);
            Point ptEnCours = new Point(hayonCourbeIntInverse.xpoints[i], hayonCourbeIntInverse.ypoints[i]);

            ptIntersection = PlanRoulotte.trouvePointSurCourbe(ptAvant, ptEnCours, cercleHayonPoutreArriereCalcul.getPathIterator(null, flatness));
            if (ptIntersection != null) {
                break;
            }
        }

        double mmRayonCourbure = rayonCourbure.getMillimetres();

        //PlanRoulotte.objectToPolygon(hayonCourbeIntInverse, cercleHayonPoutreArriereCalcul.getPathIterator(null, flatness));

        if (ptIntersection != null) {
            Arc2D.Double arcCourbe = new Arc2D.Double(pt1.x-2.0*mmRayonCourbure, pt1.y-mmRayonCourbure,mmRayonCourbure*2.0,mmRayonCourbure*2.0, 0, -2.0*Math.asin(pt1.distance(ptIntersection)/2.0/mmRayonCourbure)*180/Math.PI, Arc2D.OPEN);

            // C'est que j'ai trouver le point d'intersection
            // Je commence par calculer l'angle entre le point de départ et le point d'intersection
            // Je dois appliquer le ratioY car il y a une erreur dans le scaling, shhhh

            //double angleHayon = Math.atan2(Math.abs(pt1.y-ptIntersection.y)* ratioY, Math.abs(pt1.x-ptIntersection.x)* ratioY) ;
            double angleHayon = Math.atan2(Math.abs(pt1.y-pt2.y)* ratioY, Math.abs(pt1.x-pt2.x)* ratioY) ;



            System.out.println(pt1 + " - " + ptIntersection + " - " + Math.toDegrees(angleHayon));

            PlanRoulotte.objectToPolygon(hayonCourbeIntInverseRetour, AffineTransform.getRotateInstance(Math.toRadians(45)-angleHayon, pt1.x, pt1.y).createTransformedShape(arcCourbe).getPathIterator(null, flatness));
        }

        // Ajout au polygone de retour
        for (int i=0; i<hayonCourbeIntInverse.npoints; i++) {
            if (hayonCourbeIntInverse.ypoints[i] < hayonCourbeIntInverseRetour.ypoints[hayonCourbeIntInverseRetour.npoints-1]) {continue;}
            hayonCourbeIntInverseRetour.addPoint(hayonCourbeIntInverse.xpoints[i], hayonCourbeIntInverse.ypoints[i]);
        }

        // Ajout du point "offset" de la marge avec le plancher
        hayonCourbeIntInverse.addPoint(ptLimitePlancherHayon.x, parent.getDTOPanneau().getHauteurBrute().getMillimetres()-mmEpaisseur);

        PlanRoulotte.objectToPolygon(polyRetour, hayonCourbeIntInverseRetour.getPathIterator(null, flatness));
        // Courbe intérieur - FIN



        // ================================================
        // TRAIT DE SCIE
        // ================================================

        // Gestion du trait de scie - DÉBUT
        AffineTransform atTraitDeScie = new AffineTransform();
        double ratioXTraitScie = (hayonCourbeIntInverseRetour.getBounds().getWidth() -(1.0*mmTraitScie)) / hayonCourbeIntInverseRetour.getBounds().getWidth();
        double ratioYTraitScie = (hayonCourbeIntInverseRetour.getBounds().getHeight()-(2.0*mmTraitScie)) / hayonCourbeIntInverseRetour.getBounds().getHeight();
        atTraitDeScie.translate(Math.ceil((2.0-ratioXTraitScie)*mmTraitScie), Math.ceil((2.0-ratioYTraitScie)*mmTraitScie));
        atTraitDeScie.setToScale(ratioXTraitScie, ratioYTraitScie);

        // Je vide le polygone de gestion du trait de scie
        polyTraitDeScie = new Polygon();

        // J'ajoute le premier point (plancher)
        if (arc4CourbeExt.npoints>1) {
            polyTraitDeScie.addPoint(xPtLimitePlancherHayon + mmTraitScie, yPtLimitePlancherHayon);
            polyTraitDeScie.addPoint(xPtLimitePlancherHayon + mmTraitScie, yPtLimitePlancherHayon - mmEpaisseur - mmTraitScie);
        }
        else {
            polyTraitDeScie.addPoint(mmEpaisseur+mmTraitScie, parent.getDTOPanneau().getHauteurBrute().getMillimetres());
        }

        // J'ajoute le polygone en temps que tel
        Polygon hayonCourbeIntRetour = new Polygon();

        for (int i=hayonCourbeIntInverseRetour.npoints-1; i>=0; i--) {
            hayonCourbeIntRetour.addPoint(hayonCourbeIntInverseRetour.xpoints[i], hayonCourbeIntInverseRetour.ypoints[i]);
        }
        PlanRoulotte.objectToPolygon(polyTraitDeScie, hayonCourbeIntRetour.getPathIterator(atTraitDeScie, flatness));

        // J'ajoute le dernier point (poutre arrière)
        if (arc1CourbeExt.npoints>1) {
            polyTraitDeScie.addPoint(hayonCourbeIntRetour.xpoints[hayonCourbeIntRetour.npoints-1]+mmTraitScie, hayonCourbeIntRetour.ypoints[hayonCourbeIntRetour.npoints-1]+mmTraitScie);
            polyTraitDeScie.addPoint(hayonCourbeExt.xpoints[hayonCourbeExt.npoints-1]+mmTraitScie, hayonCourbeExt.ypoints[hayonCourbeExt.npoints-1]);
        }
        else {
            polyTraitDeScie.addPoint(mmEpaisseur+mmTraitScie, 0);
        }
        // Gestion du trait de scie - FIN


        // Application des modifications au polygone du hayon
        this.polygon = polyRetour;
    }











/*
    public void calculHayon1() {
        Polygon polyRetour = new Polygon();

        // Je vais chercher la courbe extérieure
        Polygon hayonCourbeExt = new Polygon();
        Polygon arcHayonCourbeInt = new Polygon();

        Polygon arc1 = new Polygon(Ellipse.getArc1().xpoints, Ellipse.getArc1().ypoints, Ellipse.getArc1().npoints);
        Polygon arc4 = new Polygon(Ellipse.getArc4().xpoints, Ellipse.getArc4().ypoints, Ellipse.getArc4().npoints);

        Ellipse2D.Double cercleTopHayon = new Ellipse2D.Double(0, 0, rayonCourbure.getMillimetres()*2, rayonCourbure.getMillimetres()*2);
        Ellipse2D.Double cercleTopHayonTraitDeScie = new Ellipse2D.Double(0, 0, rayonCourbure.getMillimetres()*2, rayonCourbure.getMillimetres()*2);

        int mmEpaisseur = epaisseur.getMillimetres();
        int mmTraitDeScie = traitDeScie.getMillimetres();
        double difRayonEllipse = mmEpaisseur*2.0;
        double flatness = 0.1; // pour faciliter la gestion de l'affichage des ellipses.

        AffineTransform at = new AffineTransform();


        PlanRoulotte.objectToPolygon(hayonCourbeExt, arc4.getPathIterator(null, flatness));






        //PlanRoulotte.objectToPolygon(hayonCourbeExt, arc1.getPathIterator(null, flatness));

//        polyRetour = new Polygon(hayonCourbeExt.xpoints, hayonCourbeExt.ypoints, hayonCourbeExt.npoints);
//        Ellipse ellipse1 = new Ellipse();
//        Ellipse ellipse4 = new Ellipse();
//        for (Ellipse ellipseEtude : parent.getEllipseListe()) {
//            // Pour aller former l'arc supérieur-intérieur du panneau (arc #1)
//            if (ellipseEtude.getQuadrant() == 1) {
//                Ellipse2D.Double ellipse1Ellipse = new Ellipse2D.Double(ellipseEtude.getEllipse().x, ellipseEtude.getEllipse().y, ellipseEtude.getEllipse().width, ellipseEtude.getEllipse().height);
//                ellipse1 = new Ellipse(ellipse1Ellipse, 1, UUID.randomUUID());
//            }
//            if (ellipseEtude.getQuadrant() == 4) {
//                Ellipse2D.Double ellipse4Ellipse = new Ellipse2D.Double(ellipseEtude.getEllipse().x, ellipseEtude.getEllipse().y, ellipseEtude.getEllipse().width, ellipseEtude.getEllipse().height);
//                ellipse4 = new Ellipse(ellipse4Ellipse, 4, UUID.randomUUID());
//            }
//        }
//
//        Arc2D.Double arc1Int = new Arc2D.Double(arc1.getBounds().x+mmEpaisseur,arc1.getBounds().y+mmEpaisseur,ellipse1.getEllipse().width-mmEpaisseur*2,ellipse1.getEllipse().height-mmEpaisseur*2,90,90,Arc2D.OPEN);
//        PlanRoulotte.objectToPolygon(polyRetour, arc1Int.getPathIterator(null, flatness));
//
//        Arc2D.Double arc4Int = new Arc2D.Double(ellipse4.getEllipse().x+mmEpaisseur,ellipse4.getEllipse().y+mmEpaisseur,ellipse4.getEllipse().width-mmEpaisseur*2,ellipse4.getEllipse().height-mmEpaisseur*2,180,90,Arc2D.OPEN);
//        PlanRoulotte.objectToPolygon(polyRetour, arc4Int.getPathIterator(null, flatness));



        int[] xPoints = arc1.xpoints;
        int[] yPoints = arc1.ypoints;

        Point pt1LigneArret = new Point((int)parent.getPoutreArriere().getPosition().getX()-distanceDePoutre.getMillimetres(), 0);
        for (int i = 0; i < arc1.npoints; i++) {
            int j = (i == 0) ? i + 1 : i - 1;
            if (arc1.npoints==1) {j=i;}
            Point ptAvant       = new Point(xPoints[j], yPoints[j]);
            Point ptEnCours     = new Point(xPoints[i], yPoints[i]);
            Point pt2LigneArret = new Point((int) parent.getPoutreArriere().getPosition().getX()-distanceDePoutre.getMillimetres(), Math.max(ptAvant.y, ptEnCours.y) + 1);

            Point ptIntersection = PlanRoulotteControleur.pointIntersection(ptAvant, ptEnCours, pt1LigneArret, pt2LigneArret);

            if (ptIntersection == null) {
                hayonCourbeExt.addPoint(xPoints[i], yPoints[i]);
            }
            else {
                hayonCourbeExt.addPoint(ptIntersection.x, ptIntersection.y);
            }
        }

        Point ptEtudeCourbeHayon = new Point(hayonCourbeExt.xpoints[hayonCourbeExt.npoints-1], hayonCourbeExt.ypoints[hayonCourbeExt.npoints-1]);

        polyRetour = hayonCourbeExt;

        Polygon arc1TraitDeScie = new Polygon();
        Polygon arc4TraitDeScie = new Polygon();

        Polygon hayonInterieur = new Polygon();
        Polygon hayonInterieurTraitDeScie = new Polygon();

        if (hayonCourbeExt.npoints > 2) {
            // Aller chercher la courbe intérieure
            Ellipse ellipse1;
            Ellipse ellipse4;
            Ellipse ellipse1TraitDeScie;
            Ellipse ellipse4TraitDeScie;

            Point NouveauPtCoinSupGauche = new Point(mmEpaisseur,mmEpaisseur);
            Point NouveauPtCoinSupDroit  = new Point(parent.getLongueur().getMillimetres()-mmEpaisseur,mmEpaisseur);
            Point NouveauPtCoinInfDroit  = new Point(parent.getLongueur().getMillimetres()-mmEpaisseur,parent.getHauteur().getMillimetres()-mmEpaisseur);
            Point NouveauPtCoinInfGauche = new Point(mmEpaisseur,parent.getHauteur().getMillimetres()-mmEpaisseur);

            Point NouveauPtCoinSupGaucheTraitDeScie = new Point(mmEpaisseur+traitDeScie.getMillimetres(),mmEpaisseur+traitDeScie.getMillimetres());
            Point NouveauPtCoinSupDroitTraitDeScie  = new Point(parent.getLongueur().getMillimetres()-mmEpaisseur-traitDeScie.getMillimetres(),mmEpaisseur);
            Point NouveauPtCoinInfDroitTraitDeScie  = new Point(parent.getLongueur().getMillimetres()-mmEpaisseur-traitDeScie.getMillimetres(),parent.getHauteur().getMillimetres()-mmEpaisseur);
            Point NouveauPtCoinInfGaucheTraitDeScie = new Point(mmEpaisseur+traitDeScie.getMillimetres(),parent.getHauteur().getMillimetres()-mmEpaisseur);

            for (Ellipse ellipseEtude : parent.getEllipseListe()) {
                // Pour aller former l'arc supérieur-intérieur du panneau (arc #1)
                if (ellipseEtude.getQuadrant()==1) {
                    if (Ellipse.getArc1().npoints <= 1){
                        arc1.reset();
                        arc1.addPoint(NouveauPtCoinSupGauche.x, NouveauPtCoinSupGauche.y-mmEpaisseur);

                        arc1TraitDeScie.reset();
                        arc1TraitDeScie.addPoint(NouveauPtCoinSupGaucheTraitDeScie.x, NouveauPtCoinSupGaucheTraitDeScie.y-mmEpaisseur);

                        continue;
                    }
                    Ellipse2D.Double ellipse1Ellipse = new Ellipse2D.Double(ellipseEtude.getEllipse().x, ellipseEtude.getEllipse().y, ellipseEtude.getEllipse().width, ellipseEtude.getEllipse().height);
                    ellipse1 = new Ellipse(ellipse1Ellipse, 1, UUID.randomUUID());
                    ellipse1.editEllipse(ellipse1Ellipse.x+mmEpaisseur, ellipse1Ellipse.y+mmEpaisseur, ellipse1Ellipse.width- difRayonEllipse, ellipse1Ellipse.height- difRayonEllipse);

                    // Je reconstruit une arc qui vient former la partie supérieur-intérieur du hayon
                    arc1.reset();
                    arc1.addPoint((int)ellipse1Ellipse.x+mmEpaisseur, Ellipse.getPtCoinSupGaucheDebut().y);
                    arc1 = ellipseEtude.getQuarter1(ellipse1.getPolygonComplet(), NouveauPtCoinInfGauche, NouveauPtCoinSupGauche, NouveauPtCoinSupDroit, false);

                    // Gestion du trait de scie
                    ellipse1TraitDeScie = new Ellipse(ellipse1Ellipse, 1, UUID.randomUUID());
                    ellipse1TraitDeScie.editEllipse(ellipse1Ellipse.x+mmEpaisseur+mmTraitDeScie, ellipse1Ellipse.y+mmEpaisseur+mmTraitDeScie, ellipse1Ellipse.width-difRayonEllipse-mmTraitDeScie, ellipse1Ellipse.height-difRayonEllipse-mmTraitDeScie);
                    arc1TraitDeScie = ellipseEtude.getQuarter1(ellipse1TraitDeScie.getPolygonComplet(), NouveauPtCoinInfGaucheTraitDeScie, NouveauPtCoinSupGaucheTraitDeScie, NouveauPtCoinSupDroitTraitDeScie, false);

                    // C'est pour contrôler quand le coin n'est pas pris en charge pour le calcul du hayon (ellipse non valide)
                    if (arc1.npoints <= 1) {
                        arc1.reset();
                        arc1.addPoint(NouveauPtCoinSupGauche.x, NouveauPtCoinSupGauche.y-mmEpaisseur);

                        arc1TraitDeScie.reset();
                        arc1TraitDeScie.addPoint(NouveauPtCoinSupGaucheTraitDeScie.x, NouveauPtCoinSupGaucheTraitDeScie.y-mmEpaisseur);
                    }
                }

                // Pour aller former l'arc inférieur-intérieur du panneau (arc #4)
                if (ellipseEtude.getQuadrant()==4) {
                    if (Ellipse.getArc4().npoints <= 1){
                        arc4.reset();
                        arc4.addPoint(NouveauPtCoinInfGauche.x, NouveauPtCoinInfGauche.y+mmEpaisseur);

                        arc4TraitDeScie.reset();
                        arc4TraitDeScie.addPoint(NouveauPtCoinInfGaucheTraitDeScie.x, NouveauPtCoinInfGaucheTraitDeScie.y+mmEpaisseur);

                        continue;
                    }
                    Ellipse2D.Double ellipse4Ellipse = new Ellipse2D.Double(ellipseEtude.getEllipse().x, ellipseEtude.getEllipse().y, ellipseEtude.getEllipse().width, ellipseEtude.getEllipse().height);
                    ellipse4 = new Ellipse(ellipse4Ellipse, 4, UUID.randomUUID());
                    ellipse4.editEllipse(ellipse4Ellipse.x+mmEpaisseur, ellipse4Ellipse.y+mmEpaisseur, ellipse4Ellipse.width- difRayonEllipse, ellipse4Ellipse.height- difRayonEllipse);

                    arc4.reset();
                    arc4 = ellipseEtude.getQuarter4(ellipse4.getPolygonComplet(), NouveauPtCoinInfDroit, NouveauPtCoinInfGauche, NouveauPtCoinSupGauche, false);

                    ellipse4TraitDeScie = new Ellipse(ellipse4Ellipse, 4, UUID.randomUUID());
                    ellipse4TraitDeScie.editEllipse(ellipse4Ellipse.x+mmEpaisseur+mmTraitDeScie, ellipse4Ellipse.y+mmEpaisseur-mmTraitDeScie, ellipse4Ellipse.width-difRayonEllipse-mmTraitDeScie, ellipse4Ellipse.height-difRayonEllipse-mmTraitDeScie);
                    arc4TraitDeScie = ellipseEtude.getQuarter4(ellipse4TraitDeScie.getPolygonComplet(), NouveauPtCoinInfDroitTraitDeScie, NouveauPtCoinInfGaucheTraitDeScie, NouveauPtCoinSupGaucheTraitDeScie, false);

                    if (arc4.npoints <= 1) {
                        arc4.reset();
                        arc4.addPoint(NouveauPtCoinInfGauche.x, NouveauPtCoinInfGauche.y+mmEpaisseur);

                        arc4TraitDeScie.reset();
                        arc4TraitDeScie.addPoint(NouveauPtCoinInfGaucheTraitDeScie.x, NouveauPtCoinInfGaucheTraitDeScie.y+mmEpaisseur);
                    }
                }
            }

            // Gestion du cercle en haut
            // =========================
            // Création de l'ellipse qui va servir pour la gestion de la courbure



            // Test

            // Si je place le cercle sur le bout d'un rectangle, et que je le recule jusqu'à ce qu'un point touche le coin sup droit du rectangle,
            // Je peux trouver l'angle entre le point le plus bas et le point de touche.
            // Avec ça, j'ai le extent de l'arc.
            Rectangle2D.Double rectTest = new Rectangle2D.Double(0,0,rayonCourbure.getMillimetres()*2,mmEpaisseur);
            Arc2D.Double arcIntSup = new Arc2D.Double(0,0,rayonCourbure.getMillimetres()*2, rayonCourbure.getMillimetres()*2, 0, 90, Arc2D.OPEN);











            cercleTopHayon = new Ellipse2D.Double(0, 0, rayonCourbure.getMillimetres()*2, rayonCourbure.getMillimetres()*2);

            Polygon hayonCourbeInt = new Polygon();
            Point ptIntersectionAvecCercle = null; // Va servir pour connaître le point de contact entre le bout de cercle et l'arc1


            // Déplacer l'ellipse à la bonne place
            // Je commence par le mettre sur le coin du hayon
            if (arc1.npoints>1) {
                xPoints = arc1.xpoints;
                yPoints = arc1.ypoints;

                for (int i = arc1.npoints - 1; i >= 0; i--) {
                    int j = (i == 0) ? i+1 : i-1;
                    if (i==arc4.npoints-1) {continue;}
                    Point p1 = new Point(xPoints[i], yPoints[i]);
                    Point p2 = new Point(xPoints[j], yPoints[j]);

                    // Déterminer le point de rencontre avec l'arc1. (Ça va servir pour déterminer où commencer dans l'ajout des points de l'arc1)
                    Point point = PlanRoulotte.tangenteCercleDroite(p1, p2, cercleTopHayon);

                    cercleTopHayon.x = point.x-cercleTopHayon.width/2;
                    cercleTopHayon.y = point.y-cercleTopHayon.height/2;

                    if (cercleTopHayon.contains(ptEtudeCourbeHayon)) {
                        continue;
                    }

                    // C'est que le cercle est dépasser le point ptEtudeCourbeHayon, donc il faut le ramener vers le coin
                    // Calcul des delta et al.
                    double deltaXCentrePtControle = ptEtudeCourbeHayon.x-cercleTopHayon.getCenterX();
                    double deltaYCentrePtControle = ptEtudeCourbeHayon.y-cercleTopHayon.getCenterY();

                    double distanceCentrePtControle = Math.sqrt(deltaXCentrePtControle*deltaXCentrePtControle + deltaYCentrePtControle*deltaYCentrePtControle);

                    double diffTangentePtControle = distanceCentrePtControle - cercleTopHayon.getWidth()/2.0;
                    double ratioDeplacement = (diffTangentePtControle/distanceCentrePtControle);

                    double NouvDeltaXCentrePtControle = deltaXCentrePtControle * ratioDeplacement;
                    double NouvDeltaYCentrePtControle = deltaYCentrePtControle * ratioDeplacement;

                    cercleTopHayon.x += NouvDeltaXCentrePtControle;
                    cercleTopHayon.y += NouvDeltaYCentrePtControle;

                    // Trouver le point d'intersection entre la courbe et l'arc1
                    for (int k = arc1.npoints - 1; k >= 0; k--) {
                        j = (i == 0) ? i+1 : i-1;
                        if (i==arc4.npoints-1) {continue;}
                        ptIntersectionAvecCercle = PlanRoulotte.trouvePointSurCourbe(new Point(xPoints[i], yPoints[i]), new Point(xPoints[j], yPoints[j]), cercleTopHayon.getPathIterator(null), flatness);
                        if (ptIntersectionAvecCercle != null) {break;}
                    }

                    if (ptIntersectionAvecCercle == null) {break;}

                    // Ajouter la trace de l'ellipse au hayon
                    FlatteningPathIterator iterator = new FlatteningPathIterator(cercleTopHayon.getPathIterator(null), flatness);
                    float[] coordonnees = new float[6];

                    while (!iterator.isDone()) {
                        int temoin = iterator.currentSegment(coordonnees);
                        int x = (int) coordonnees [0];
                        int y = (int) coordonnees [1];
                        // Découper l'ellipse
                        if (y>=ptEtudeCourbeHayon.y && x>=ptIntersectionAvecCercle.x) {
                            arcHayonCourbeInt.addPoint(x,y);
                        }
                        Point ptTest = new Point(Math.round(x),Math.round(y));
                        if (ptTest.x == ptIntersectionAvecCercle.x && ptTest.y == ptIntersectionAvecCercle.y) {
                            break;
                        }
                        iterator.next();
                    }
                    break;
                }
            }

            // Ajout de l'arc du hayon
            PlanRoulotte.objectToPolygon(hayonCourbeInt, arcHayonCourbeInt.getPathIterator(null, flatness));
            // Ajout des arcs au hayon

            xPoints = arc1.xpoints;
            yPoints = arc1.ypoints;

            for (int i = arc1.npoints-1; i>=0; i--) {
                if (ptIntersectionAvecCercle != null && xPoints[i]>=ptIntersectionAvecCercle.x) {continue;}
                hayonCourbeInt.addPoint(xPoints[i], yPoints[i]);
            }

            xPoints = arc4.xpoints;
            yPoints = arc4.ypoints;
            pt1LigneArret = new Point(Ellipse.getArc4().xpoints[0], 0);
            for (int i = arc4.npoints-1; i>=0; i--) {
                if (xPoints[i] > Ellipse.getArc4().xpoints[0] && arc4.npoints > 1) {
                    int j = (i == 0) ? i+1 : i-1;
                    if (i==arc4.npoints-1) {j=i;}
                    Point ptSuivant     = new Point(xPoints[j], yPoints[j]);
                    Point ptEnCours     = new Point(xPoints[i], yPoints[i]);
                    Point pt2LigneArret = new Point(Ellipse.getArc4().xpoints[0], parent.getHauteur().getMillimetres());

                    Point ptIntersection = PlanRoulotteControleur.pointIntersection(ptSuivant, ptEnCours, pt1LigneArret, pt2LigneArret);

                    if (ptIntersection != null) {
                        hayonCourbeInt.addPoint(ptIntersection.x, ptIntersection.y);
                        continue;
                    }
                }

                hayonCourbeInt.addPoint(xPoints[i], yPoints[i]);
            }

            PlanRoulotte.objectToPolygon(polyRetour, hayonCourbeInt.getPathIterator(null));
        }


        // DÉBUT : Gestion du trait de scie
        // Pour la gestion du bas du trait de scie


        polyTraitDeScie.reset();
        if (arc4TraitDeScie.npoints>1) {
            // Si j'ai un arc dans le coin inférieur, je dois faire afficher le trait de scie
            polyTraitDeScie.addPoint(Ellipse.getPtCoinInfGaucheDebut().x+mmTraitDeScie, Ellipse.getPtCoinInfGaucheDebut().y);
            polyTraitDeScie.addPoint(Ellipse.getPtCoinInfGaucheDebut().x+mmTraitDeScie, arc4.ypoints[0]-mmTraitDeScie);
            xPoints = arc4TraitDeScie.xpoints;
            yPoints = arc4TraitDeScie.ypoints;
            for (int i = 0; i < arc4TraitDeScie.npoints; i++) {
                if (xPoints[i] > Ellipse.getPtCoinInfGaucheDebut().x + mmTraitDeScie) {
                    continue;
                }
                polyTraitDeScie.addPoint(xPoints[i], yPoints[i]);
            }
        }
        else {
            // Si je n'ai pas d'arc de cercle dans le coin inférieur, on retourne le bas du panneau + épaisseur hayon
            // TODO : on peut gérer ici si le hayon est optionnel
            polyTraitDeScie.addPoint(mmEpaisseur+mmTraitDeScie, parent.getHauteur().getMillimetres());
        }

        // Gestion de la courbure du top du hayon - Trait de scie
        // Pour la gestion du haut du trait de scie
        if (arc1TraitDeScie.npoints>1) {


            xPoints = arc1TraitDeScie.xpoints;
            yPoints = arc1TraitDeScie.ypoints;

            Polygon test = new Polygon(arcHayonCourbeInt.xpoints, arcHayonCourbeInt.ypoints, arcHayonCourbeInt.npoints);

            AffineTransform at2 = new AffineTransform();
            at2.scale(2,2);
            Shape test2 = at2.createTransformedShape(test);

            for (int i = 0; i < arc1TraitDeScie.npoints; i++) {
                if (xPoints[i] > Ellipse.getPtCoinSupGaucheFin().x + mmTraitDeScie) {
                    continue;
                }
                polyTraitDeScie.addPoint(xPoints[i], yPoints[i]);
            }

            //polyTraitDeScie.addPoint(Ellipse.getPtCoinSupGaucheFin().x+mmTraitDeScie, Ellipse.getPtCoinSupGaucheFin().y+mmEpaisseur+mmTraitDeScie);
            //polyTraitDeScie.addPoint(Ellipse.getPtCoinSupGaucheFin().x+mmTraitDeScie, Ellipse.getPtCoinSupGaucheFin().y);
        }
        else {
            // Si je n'ai pas d'arc de cercle dans le coin supérieur, on retourne le top du panneau
            // Si j'ai une arc dans le bas
            if (arc4TraitDeScie.npoints>1) {
                polyTraitDeScie.addPoint(polyTraitDeScie.xpoints[polyTraitDeScie.npoints], 0);
            }
            else { // C'est que je n'ai aucune arc également dans le bas
                // TODO : on peut gérer ici si le hayon est optionnel
                polyTraitDeScie.addPoint(mmEpaisseur+mmTraitDeScie, 0);
            }
        }
        // ==============================
        // FIN : Gestion du trait de scie


        // Retour du polygon du hayon au hayon


        this.polygon = polyRetour;
    }
*/

    public Mesure getDistanceDePoutre() {return distanceDePoutre;}

    public Mesure getDistanceDuPlancher() {return distanceDuPlancher;}

    public Mesure getEpaisseur() {return epaisseur;}

    public Double getPoids() {return poids;}

    public Mesure getRayonCourbure() {return rayonCourbure;}

    public Mesure getTraitDeScie() {return traitDeScie;}

    public Polygon getPolyTraitDeScie() {return polyTraitDeScie;}

    public void setEpaisseur(Mesure epaisseur){
        this.epaisseur = epaisseur;
    }

    public void setDistanceDePoutre(Mesure distanceDePoutre){
        this.distanceDePoutre = distanceDePoutre;
    }

    public void setDistanceDuPlancher(Mesure distanceDuPlancher){
        this.distanceDuPlancher = distanceDuPlancher;
    }

    public void setTraitDeScie(Mesure traitDeScie){
        this.traitDeScie = traitDeScie;
    }

    public void setPoids(Double poids){
        this.poids = poids;
    }

    public void setRayonCourbure(Mesure rayonCourbure){
        this.rayonCourbure = rayonCourbure;
    }

    public boolean isSelected(){return this.selectionStatus;}

    public Point getPointTopHayon() {return this.pointTopHayon;} // TODO : À AJOUTER AU UML

    public Point getPointBotHayon() {return this.pointBotHayon;} // TODO : À AJOUTER AU UML

    @Override
    public void translate(Point2D.Double delta) {
    }

    @Override
    public void switchSelection() {this.selectionStatus = !this.selectionStatus;}

    @Override
    public boolean estInterne() {
        return false;
    }

    @Override
    public boolean estExterne() {
        return false;
    }

    @Override
    public DTOHayon getElementDTO() {
        return new DTOHayon(distanceDePoutre, epaisseur, distanceDuPlancher,
                traitDeScie, poids, rayonCourbure, selectionStatus);
    }

    @Override
    public Polygon getPolygon() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return polygon;
    }

    @Override
    public void calculDisposition() {
        Polygon polyRetour = new Polygon();

        double flatness = 0.1;
        int mmDebutPlancher = parent.getDTOPlancher().getMargeArriere().getMillimetres();
        int mmEpaisseur = this.epaisseur.getMillimetres();
        int mmDistanceDuPlancher = this.distanceDuPlancher.getMillimetres();
        int mmDistanceDePoutre = this.distanceDePoutre.getMillimetres();
        int mmDistanceDebutPlancher = parent.getDTOPlancher().getMargeArriere().getMillimetres();
        int mmPositionPoutreArr = parent.getDTOPoutreArriere().getPositionRelative().getMillimetres();
        int mmHauteurRoulotte = parent.getDTOPanneau().getHauteurBrute().getMillimetres();
        int mmLargeurRoulotte = parent.getDTOPanneau().getLargeurBrute().getMillimetres();
        boolean poutrePlusLoinQueEllipse = false;

        Point ptDebutArc4 = new Point();
        Point ptFinArc4 = new Point();
        Point ptDebutArc1 = new Point();
        Point ptFinArc1 = new Point();

        Polygon copieArc1 = new Polygon(Ellipse.getArc1().xpoints, Ellipse.getArc1().ypoints, Ellipse.getArc1().npoints);
        Polygon copieArc4 = new Polygon(Ellipse.getArc4().xpoints, Ellipse.getArc4().ypoints, Ellipse.getArc4().npoints);

        // Pour le début du plancher
        polyRetour.addPoint(mmDistanceDebutPlancher-mmDistanceDuPlancher, mmHauteurRoulotte);

        // Hayon extérieur
        for (int i=0; i<copieArc4.npoints; i++) {
            if (copieArc4.xpoints[i] > (mmDebutPlancher-mmDistanceDuPlancher)) {
                continue;
            }
            polyRetour.addPoint(copieArc4.xpoints[i], copieArc4.ypoints[i]);
            if (polyRetour.npoints==2) {
                ptDebutArc4 = new Point(copieArc4.xpoints[i], copieArc4.ypoints[i]);
            }
        }

        ptFinArc4 = new Point(polyRetour.xpoints[polyRetour.npoints-1], polyRetour.ypoints[polyRetour.npoints-1]);

        Polygon copiePoutreArriere = new Polygon(parent.getPoutreArriere().getPolygon().xpoints, parent.getPoutreArriere().getPolygon().ypoints, parent.getPoutreArriere().getPolygon().npoints);

        int posXMaxPoutreArr = Integer.MAX_VALUE;

        for (int i=0; i<copiePoutreArriere.npoints; i++) {
            if (copiePoutreArriere.xpoints[i] < posXMaxPoutreArr) {posXMaxPoutreArr = copiePoutreArriere.xpoints[i];}
        }

        int nbPointPolyRetour = polyRetour.npoints;
        for (int i=0; i<copieArc1.npoints; i++) {
            if (copieArc1.xpoints[i] > (posXMaxPoutreArr-mmDistanceDuPlancher)) {
                continue;
            }
            polyRetour.addPoint(copieArc1.xpoints[i], copieArc1.ypoints[i]);
            if (polyRetour.npoints == nbPointPolyRetour+1) {
                ptDebutArc1 = new Point(copieArc1.xpoints[i], copieArc1.ypoints[i]);
            }
        }

        ptFinArc1 = new Point(polyRetour.xpoints[polyRetour.npoints-1], polyRetour.ypoints[polyRetour.npoints-1]);


        // Ajout du point pour le cas où la poutre arrière n'est pas dans l'ellipse
        if (mmPositionPoutreArr > copieArc1.xpoints[copieArc1.npoints-1]) {
            poutrePlusLoinQueEllipse = true;
            polyRetour.addPoint(mmPositionPoutreArr-mmDistanceDePoutre, 0);
        }

        this.pointBotHayon = new Point(ptDebutArc4);
        this.pointTopHayon = new Point(polyRetour.xpoints[polyRetour.npoints-1], polyRetour.ypoints[polyRetour.npoints-1]);


        // =========================================
        // Gestion pour la courbe intérieur du hayon
        // =========================================
        Arc2D.Double arcInt1 = new Arc2D.Double();
        Arc2D.Double arcInt4 = new Arc2D.Double();


        for (Ellipse ellipse : parent.getEllipseListe()) {
            if (ellipse.getQuadrant() == 1) {
                arcInt1 = new Arc2D.Double(ellipse.getEllipse().getX()+mmEpaisseur, ellipse.getEllipse().getY()+mmEpaisseur, 2.0*(ellipse.getPolygon().getBounds().getWidth()-mmEpaisseur),2.0*(ellipse.getPolygon().getBounds().getHeight()-mmEpaisseur), 90, 90, Arc2D.OPEN);
            }
            if (ellipse.getQuadrant() == 4) {
                arcInt4 = new Arc2D.Double(ellipse.getEllipse().getX()+mmEpaisseur, ellipse.getEllipse().getY()+mmEpaisseur, 2.0*(ellipse.getPolygon().getBounds().getWidth()-mmEpaisseur),2.0*(ellipse.getPolygon().getBounds().getHeight()-mmEpaisseur), 180, 90, Arc2D.OPEN);
            }
        }

        Polygon polyArcInt1 = new Polygon();
        Polygon polyArcInt4 = new Polygon();
        PlanRoulotte.objectToPolygon(polyArcInt1, arcInt1.getPathIterator(null, flatness));
        PlanRoulotte.objectToPolygon(polyArcInt4, arcInt4.getPathIterator(null, flatness));

        nbPointPolyRetour = polyRetour.npoints;


        // Gestion de la courbure du top du hayon
        int mmRayonCourbure = this.rayonCourbure.getMillimetres();
        Ellipse2D.Double courbureHayon = new Ellipse2D.Double(this.pointTopHayon.x-mmRayonCourbure, this.pointTopHayon.y-mmRayonCourbure, mmRayonCourbure*2, mmRayonCourbure*2);

        double distTest = Math.sqrt(2*Math.pow(mmEpaisseur, 2));
        Ellipse2D.Double courbureHayonTest = new Ellipse2D.Double(this.pointTopHayon.x-distTest, this.pointTopHayon.y-distTest, distTest*2, distTest*2);

        Point ptCourbure1 = new Point();
        Point ptCourbure2 = new Point();
        Point ptIntersectionCourbure = new Point();

        if (poutrePlusLoinQueEllipse) {
            ptCourbure1 = new Point(this.pointTopHayon.x, mmEpaisseur);
            ptCourbure2 = new Point(this.pointTopHayon.x-(int)distTest, mmEpaisseur);
            ptIntersectionCourbure = PlanRoulotte.trouvePointSurCourbe(ptCourbure1, ptCourbure2, polyArcInt1.getPathIterator(null, flatness));
            if (ptIntersectionCourbure == null) {
                ptIntersectionCourbure = new Point(this.pointTopHayon.x-mmEpaisseur, mmEpaisseur);
            }
        }
        else {
            Point ptAvant = new Point(polyArcInt1.xpoints[0], polyArcInt1.ypoints[0]);
            for (int i=1; i<polyArcInt1.npoints; i++) {
                int x = polyArcInt1.xpoints[i];
                int y = polyArcInt1.ypoints[i];
                Point ptApres = new Point(x,y);
                Point ptIntersect = PlanRoulotte.trouvePointSurCourbe(ptAvant, ptApres, courbureHayonTest.getPathIterator(null, flatness));
                if (ptIntersect != null) {
                    ptIntersectionCourbure = new Point(x,y);
                }
                ptAvant = new Point(ptApres);
            }
        }

        double angleCourbure = 2.0*Math.asin((distTest/(2*mmRayonCourbure)));

        Arc2D.Double arcCourbure = new Arc2D.Double(0, 0, mmRayonCourbure*2, mmRayonCourbure*2, 0, Math.toDegrees(angleCourbure), Arc2D.OPEN);

        arcCourbure.x = ptIntersectionCourbure.x-arcCourbure.width;
        arcCourbure.y = ptIntersectionCourbure.y-arcCourbure.height/2;

        double angleArc = Math.PI/2-(angleCourbure/2);
        double anglePtIntersectionCoin = Math.atan2(Math.abs(this.pointTopHayon.y- ptIntersectionCourbure.y), Math.abs(this.pointTopHayon.x- ptIntersectionCourbure.x));

        double diffAngle = Math.PI-anglePtIntersectionCoin-angleArc;

        AffineTransform at = new AffineTransform();
        at.rotate(diffAngle, ptIntersectionCourbure.x, ptIntersectionCourbure.y);

        Polygon tampon = new Polygon();
        PlanRoulotte.objectToPolygon(tampon, arcCourbure.getPathIterator(at, flatness));

        for (int i=tampon.npoints-1; i>=0; i--) {
            int x = tampon.xpoints[i];
            int y = tampon.ypoints[i];
            polyRetour.addPoint(x,y);
        }

        Point ptLimiteCourbure = new Point(polyRetour.xpoints[polyRetour.npoints-1], polyRetour.ypoints[polyRetour.npoints-1]);

        Point pt1 = new Point(ptFinArc1);
        Point pt2 = new Point((int)arcInt1.getCenterX(), (int)arcInt1.getCenterY());
        Point ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyArcInt1.getPathIterator(null, flatness));
        if (ptIntersection != null) {polyRetour.addPoint(ptIntersection.x, mmEpaisseur);}

        // Ajout de l'arc#1
        nbPointPolyRetour = polyRetour.npoints;
        for (int i=0; i<polyArcInt1.npoints; i++) {
            int x = polyArcInt1.xpoints[i];
            int y = polyArcInt1.ypoints[i];
            if (x < mmEpaisseur || x > ptFinArc1.x) {continue;}
            if (x>ptLimiteCourbure.x) {continue;}
            polyRetour.addPoint(x, y);
        }

        // Aucun points n'a été ajouté
        if (nbPointPolyRetour == polyRetour.npoints) {
            if (poutrePlusLoinQueEllipse) {
                polyRetour.xpoints[polyRetour.npoints-1] = mmEpaisseur;
                polyRetour.ypoints[polyRetour.npoints-1] = mmEpaisseur;
            }
            else {
                polyRetour.addPoint(mmEpaisseur, 0);
            }
        }

        // Pour déterminer le point de fin de la courbe de l'arc #1 intérieur
        pt1 = new Point(ptDebutArc1);
        pt2 = new Point((int)arcInt1.getCenterX(), (int)arcInt1.getCenterY());
        ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyArcInt1.getPathIterator(null, flatness));
        if (ptIntersection != null) {polyRetour.addPoint(mmEpaisseur, ptIntersection.y);}


        // Arc4 intérieur
        pt1 = new Point(ptFinArc4);
        pt2 = new Point((int)arcInt4.getCenterX(), (int)arcInt4.getCenterY());
        ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyArcInt4.getPathIterator(null, flatness));
        if (ptIntersection != null) {polyRetour.addPoint(mmEpaisseur, ptIntersection.y);}

        nbPointPolyRetour = polyRetour.npoints;
        for (int i=0; i<polyArcInt4.npoints; i++) {
            int x = polyArcInt4.xpoints[i];
            int y = polyArcInt4.ypoints[i];
            if (x < mmEpaisseur || x > ptDebutArc4.x) {continue;}
            polyRetour.addPoint(x, y);
        }

        // Aucun points n'a été ajouté
        if (nbPointPolyRetour == polyRetour.npoints) {
            polyRetour.addPoint(mmEpaisseur, mmHauteurRoulotte-mmEpaisseur);
        }
        else {
            pt1 = new Point(polyRetour.xpoints[0], mmHauteurRoulotte);
            pt2 = new Point(polyRetour.xpoints[0], 0);

            ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyArcInt4.getPathIterator(null, flatness));
            if (ptIntersection != null) {polyRetour.addPoint(ptIntersection.x, ptIntersection.y);}
        }

        polyRetour.addPoint(mmDistanceDebutPlancher-mmDistanceDuPlancher, mmHauteurRoulotte-mmEpaisseur);

        this.polygon = polyRetour;


        // =============================
        // Gestion pour le trait de scie
        // =============================
        Polygon polyRetourTraitScie = new Polygon();

        int mmTraitDeScie = this.traitDeScie.getMillimetres();

        Arc2D.Double arcInt1TraitDeScie = new Arc2D.Double();
        Arc2D.Double arcInt4TraitDeScie = new Arc2D.Double();

        for (Ellipse ellipse : parent.getEllipseListe()) {
            if (ellipse.getQuadrant() == 1) {
                arcInt1TraitDeScie = new Arc2D.Double(ellipse.getEllipse().getX()+mmEpaisseur+mmTraitDeScie, ellipse.getEllipse().getY()+mmEpaisseur+mmTraitDeScie, 2.0*(ellipse.getPolygon().getBounds().getWidth()-mmEpaisseur-mmTraitDeScie),2.0*(ellipse.getPolygon().getBounds().getHeight()-mmEpaisseur-mmTraitDeScie), 90, 90, Arc2D.OPEN);
            }
            if (ellipse.getQuadrant() == 4) {
                arcInt4TraitDeScie = new Arc2D.Double(ellipse.getEllipse().getX()+mmEpaisseur+mmTraitDeScie, ellipse.getEllipse().getY()+mmEpaisseur+mmTraitDeScie, 2.0*(ellipse.getPolygon().getBounds().getWidth()-mmEpaisseur-mmTraitDeScie),2.0*(ellipse.getPolygon().getBounds().getHeight()-mmEpaisseur-mmTraitDeScie), 180, 90, Arc2D.OPEN);
            }
        }
        Polygon polyArcInt4TraitDeScie = new Polygon();
        Polygon polyArcInt1TraitDeScie = new Polygon();
        PlanRoulotte.objectToPolygon(polyArcInt4TraitDeScie, arcInt4TraitDeScie.getPathIterator(null, flatness));
        PlanRoulotte.objectToPolygon(polyArcInt1TraitDeScie, arcInt1TraitDeScie.getPathIterator(null, flatness));


        // Arc#4 (bas)
        polyRetourTraitScie.addPoint(ptDebutArc4.x+mmTraitDeScie, ptDebutArc4.y);

        if (ptDebutArc4.x != ptFinArc4.x || ptDebutArc4.y != ptFinArc4.y) { // C'est que je n'ai pas un hayon avec une courbe nulle en bas
            pt1 = new Point(ptDebutArc4);
            pt2 = new Point(ptDebutArc4.x, 0);
            ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyArcInt4TraitDeScie.getPathIterator(null, flatness));
            if (ptIntersection != null) {
                polyRetourTraitScie.addPoint(ptIntersection.x+mmTraitDeScie, ptIntersection.y);
            }
            else {
                polyRetourTraitScie.addPoint(ptDebutArc4.x+mmTraitDeScie, ptDebutArc4.y-mmEpaisseur-mmTraitDeScie);
            }
        }

        nbPointPolyRetour = polyRetourTraitScie.npoints;
        for (int i=polyArcInt4TraitDeScie.npoints-1; i>=0; i--) {
            int x = polyArcInt4TraitDeScie.xpoints[i];
            int y = polyArcInt4TraitDeScie.ypoints[i];
            if (x < mmEpaisseur+mmTraitDeScie || x > ptDebutArc4.x) {continue;}
            polyRetourTraitScie.addPoint(x, y);
        }

        if (polyRetourTraitScie.npoints == nbPointPolyRetour) {
            polyRetourTraitScie.addPoint(mmEpaisseur+mmTraitDeScie, mmHauteurRoulotte);
        }

        // Arc#1 (haut)

        // Gestion de la courbure du top du hayon
        int mmRayonCourbureTraitDeScie = this.rayonCourbure.getMillimetres() + this.traitDeScie.getMillimetres();
        int mmEpaisseurTraitDeScie = mmEpaisseur +mmTraitDeScie;
        Ellipse2D.Double courbureHayonTraitDeScie = new Ellipse2D.Double(this.pointTopHayon.x-mmRayonCourbureTraitDeScie, this.pointTopHayon.y-mmRayonCourbureTraitDeScie, mmRayonCourbureTraitDeScie*2, mmRayonCourbureTraitDeScie*2);
        double distTestTraitDeScie = Math.sqrt(2*Math.pow(mmEpaisseurTraitDeScie +mmTraitDeScie, 2));
        Ellipse2D.Double courbureHayonTestTraitDeScie = new Ellipse2D.Double(this.pointTopHayon.x- distTestTraitDeScie, this.pointTopHayon.y- distTestTraitDeScie, distTestTraitDeScie *2, distTestTraitDeScie *2);

        Point ptCourbure1TraitDeScie = new Point();
        Point ptCourbure2TraitDeScie = new Point();
        Point ptIntersectionCourbureTraitDeScie = new Point();

        if (poutrePlusLoinQueEllipse) {
            ptCourbure1TraitDeScie = new Point(this.pointTopHayon.x, mmEpaisseurTraitDeScie);
            ptCourbure2TraitDeScie = new Point(this.pointTopHayon.x-(int) distTestTraitDeScie, mmEpaisseurTraitDeScie);
            ptIntersectionCourbureTraitDeScie = PlanRoulotte.trouvePointSurCourbe(ptCourbure1TraitDeScie, ptCourbure2TraitDeScie, polyArcInt1.getPathIterator(null, flatness));
            if (ptIntersectionCourbureTraitDeScie == null) {
                ptIntersectionCourbureTraitDeScie = new Point(this.pointTopHayon.x- mmEpaisseurTraitDeScie, mmEpaisseurTraitDeScie);
            }
        }
        else {
            Point ptAvant = new Point(polyArcInt1.xpoints[0], polyArcInt1.ypoints[0]);
            for (int i=1; i<polyArcInt1.npoints; i++) {
                int x = polyArcInt1.xpoints[i];
                int y = polyArcInt1.ypoints[i];
                Point ptApres = new Point(x,y);
                Point ptIntersect = PlanRoulotte.trouvePointSurCourbe(ptAvant, ptApres, courbureHayonTestTraitDeScie.getPathIterator(null, flatness));
                if (ptIntersect != null) {
                    ptIntersectionCourbureTraitDeScie = new Point(x,y);
                }
                ptAvant = new Point(ptApres);
            }
        }

        double angleCourbureTraitDeScie = 2.0*Math.asin((distTestTraitDeScie /(2*mmRayonCourbure)));

        Arc2D.Double arcCourbureTraitDeScie = new Arc2D.Double(0, 0, mmRayonCourbure*2, mmRayonCourbure*2, 0, Math.toDegrees(angleCourbureTraitDeScie), Arc2D.OPEN);

        arcCourbureTraitDeScie.x = ptIntersectionCourbureTraitDeScie.x- arcCourbureTraitDeScie.width;
        arcCourbureTraitDeScie.y = ptIntersectionCourbureTraitDeScie.y- arcCourbureTraitDeScie.height/2;

        double angleArcTraitDeScie = Math.PI/2-(angleCourbureTraitDeScie /2);
        double anglePtIntersectionCoinTraitDeScie = Math.atan2(Math.abs(this.pointTopHayon.y- ptIntersectionCourbureTraitDeScie.y), Math.abs(this.pointTopHayon.x- ptIntersectionCourbureTraitDeScie.x));

        double diffAngleTraitDeScie = Math.PI- anglePtIntersectionCoinTraitDeScie - angleArcTraitDeScie;

        AffineTransform atTraitDeScie = new AffineTransform();
        atTraitDeScie.rotate(diffAngleTraitDeScie, ptIntersectionCourbureTraitDeScie.x, ptIntersectionCourbureTraitDeScie.y);

        Polygon tamponTraitDeScie = new Polygon();
        PlanRoulotte.objectToPolygon(tamponTraitDeScie, arcCourbureTraitDeScie.getPathIterator(atTraitDeScie, flatness));

        Point ptLimiteCourbureTraitDeScie = new Point(tamponTraitDeScie.xpoints[0], tamponTraitDeScie.ypoints[0]);

        nbPointPolyRetour = polyRetourTraitScie.npoints;
        for (int i=polyArcInt1TraitDeScie.npoints-1; i>=0; i--) {
            int x = polyArcInt1TraitDeScie.xpoints[i];
            int y = polyArcInt1TraitDeScie.ypoints[i];
            if (x < mmEpaisseurTraitDeScie || x > ptFinArc1.x) {continue;}
            if (x>ptLimiteCourbureTraitDeScie.x) {
                // C'est que nous avons atteint le début de la courbure
                continue;
            }
            polyRetourTraitScie.addPoint(x, y);
        }

        if (polyRetourTraitScie.npoints == nbPointPolyRetour) {
            polyRetourTraitScie.addPoint(mmEpaisseurTraitDeScie, mmEpaisseurTraitDeScie);
        }

        pt1 = new Point(ptFinArc1);
        pt2 = new Point((int)arcInt1.getCenterX(), (int)arcInt1.getCenterY());
        ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyArcInt1TraitDeScie.getPathIterator(null, flatness));
        if (ptIntersection != null) {polyRetourTraitScie.addPoint(ptIntersection.x, mmEpaisseurTraitDeScie);}

        // Ajouter la courbure
        for (int i=0; i<tamponTraitDeScie.npoints; i++) {
            int x = tamponTraitDeScie.xpoints[i];
            int y = tamponTraitDeScie.ypoints[i];
            if (y<0) {break;}
            polyRetourTraitScie.addPoint(x,y);
        }

        if (poutrePlusLoinQueEllipse) {
            pt1 = new Point(0, 0);
            pt2 = new Point(mmLargeurRoulotte,0);
            ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, tamponTraitDeScie.getPathIterator(null, flatness));

            if (ptIntersection != null) {polyRetourTraitScie.addPoint(ptIntersection.x, ptIntersection.y);}
        }

        this.polyTraitDeScie = polyRetourTraitScie;

        dispositionValide = true;
    }

    @Override
    public boolean dispositionAJour() {
        return this.dispositionValide;
    }

    @Override
    public void invalideLaDisposition() {
        this.dispositionValide = false;
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.HAYON;}
}