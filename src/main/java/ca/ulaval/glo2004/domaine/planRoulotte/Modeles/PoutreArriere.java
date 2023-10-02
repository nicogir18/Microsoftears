package ca.ulaval.glo2004.domaine.planRoulotte.Modeles;

import ca.ulaval.glo2004.InterfaceUtilisateur.DrawingPanel;
import ca.ulaval.glo2004.domaine.Afficheur.PlanRoulotteDessineur;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOEllipse;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPoutreArriere;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotte;
//import jdk.javadoc.internal.doclets.toolkit.MemberWriter;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.ArrayList;

public class PoutreArriere extends ElementSelectionnable implements Serializable {

    private Mesure largeur;
    private Mesure hauteur;
    private Mesure positionRelative;
    private Point position;
    private Polygon polygon;
    private PlanRoulotte parent;
    private final int prioriteAffichage = 1;


    public PoutreArriere(Mesure largeur, Mesure hauteur, Mesure positionPoutreArriere, Point position, PlanRoulotte parent){
        super(false, false);
        this.largeur = largeur;
        this.hauteur = hauteur;
        positionRelative = positionPoutreArriere;
        this.position = position;
        this.parent = parent;
        this.polygon = new Polygon(new int[]{0, 0, largeur.getMillimetres(), largeur.getMillimetres()}, new int[]{0, hauteur.getMillimetres(), hauteur.getMillimetres(), 0}, 4);
    }

    public PoutreArriere(PlanRoulotte parent){
        super(false, false);
        this.largeur = new Mesure(2, 0, 0);
        this.hauteur = new Mesure(2, 0, 0);
        position = new Point(10, 0);
        this.parent = parent;
        this.polygon = new Polygon();
    }

    public void setPositionRelative(Mesure nouvellePosition) {positionRelative = nouvellePosition;}
    public void setHauteur(Mesure hauteur){this.hauteur = hauteur;}
    public void setLargeur(Mesure largeur){this.largeur = largeur;}

    public Point getPosition() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return position;
    }

    public Mesure getLargeur(){return largeur;}

    public Mesure getHauteur(){return hauteur;}

    @Override
    public boolean isSelected(){return this.selectionStatus;}

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
        return true;
    }

    @Override
    public DTOPoutreArriere getElementDTO() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return new DTOPoutreArriere(largeur,hauteur,position,selectionStatus, positionRelative);
    }

    @Override
    public Polygon getPolygon() {
        if (!dispositionAJour()){
            calculDisposition();
        }
        return polygon;
    }

    @Override
    public boolean dispositionAJour() {
        return dispositionValide;
    }

    @Override
    public void invalideLaDisposition() {
        this.dispositionValide = false;
    }

    @Override
    public void calculDisposition() {
        Polygon polyRetour = new Polygon();

        Point ptCoinSupGauchePoutreArr = new Point();

        double flatness = 0.1;
        int mmPositionRelative = this.positionRelative.getMillimetres();
        int mmHauteurRoulotte = this.parent.getDTOPanneau().getHauteurBrute().getMillimetres();
        int mmLargeurPoutre = this.largeur.getMillimetres();
        int mmHauteurPoutre = this.hauteur.getMillimetres();

        Polygon polyTopRoulotte = new Polygon();
        PlanRoulotte.objectToPolygon(polyTopRoulotte, Ellipse.getArc1().getPathIterator(null, flatness));
        PlanRoulotte.objectToPolygon(polyTopRoulotte, Ellipse.getArc2().getPathIterator(null, flatness));

        Point pt1 = new Point(mmPositionRelative, 0);
        Point pt2 = new Point(mmPositionRelative, mmHauteurRoulotte);

        Point ptIntersection = PlanRoulotte.trouvePointSurCourbe(pt1, pt2, polyTopRoulotte.getPathIterator(null, flatness));

        if (ptIntersection != null) {
            ptCoinSupGauchePoutreArr = new Point(ptIntersection);
        }

        Ellipse2D.Double cercleCalcul = new Ellipse2D.Double(ptCoinSupGauchePoutreArr.x - mmLargeurPoutre, ptCoinSupGauchePoutreArr.y-mmLargeurPoutre, mmLargeurPoutre*2, mmLargeurPoutre*2);

        ArrayList<Point> lPoint = new ArrayList<>();

        Point ptAvant = new Point(polyTopRoulotte.xpoints[0], polyTopRoulotte.ypoints[0]);
        for (int i=1; i<polyTopRoulotte.npoints; i++) {
            int x = polyTopRoulotte.xpoints[i];
            int y = polyTopRoulotte.ypoints[i];
            Point ptApres = new Point(x,y);
            Point ptIntersect = PlanRoulotte.trouvePointSurCourbe(ptAvant, ptApres, cercleCalcul.getPathIterator(null, flatness));
            if (ptIntersect != null) {
                lPoint.add(ptIntersect);
            }
            ptAvant = new Point(ptApres);
        }

        int xTest = Integer.MIN_VALUE;
        int indice = -1;
        for (int i = 0; i < lPoint.size(); i++) {
            if (lPoint.get(i) == null) {continue;}
            Point pt = new Point(lPoint.get(i));

            if (pt.x > xTest) {
                xTest = pt.x;
                indice = i;
            }
        }

        if (indice>=0) {
            Point ptCoinSupDroitPoutreArr = new Point(lPoint.get(indice));

            polyRetour.addPoint(ptCoinSupGauchePoutreArr.x, ptCoinSupGauchePoutreArr.y);
            polyRetour.addPoint(ptCoinSupDroitPoutreArr.x, ptCoinSupDroitPoutreArr.y);

            double angle = Math.PI/2 - Math.atan2(Math.abs(ptCoinSupGauchePoutreArr.y-ptCoinSupDroitPoutreArr.y), Math.abs(ptCoinSupGauchePoutreArr.x-ptCoinSupDroitPoutreArr.x));

            double deltaX = mmHauteurPoutre * Math.cos(angle);
            double deltaY = mmHauteurPoutre * Math.sin(angle);

            polyRetour.addPoint(ptCoinSupDroitPoutreArr.x+(int)deltaX, ptCoinSupDroitPoutreArr.y+(int)deltaY);
            polyRetour.addPoint(ptCoinSupGauchePoutreArr.x+(int)deltaX, ptCoinSupGauchePoutreArr.y+(int)deltaY);

        }
        else { // C'est que la poutre arrière n'est pas dans la courbe
            polyRetour.addPoint(ptCoinSupGauchePoutreArr.x, ptCoinSupGauchePoutreArr.y);
            polyRetour.addPoint(ptCoinSupGauchePoutreArr.x+mmLargeurPoutre, ptCoinSupGauchePoutreArr.y);
            polyRetour.addPoint(ptCoinSupGauchePoutreArr.x+mmLargeurPoutre, ptCoinSupGauchePoutreArr.y+mmHauteurPoutre);
            polyRetour.addPoint(ptCoinSupGauchePoutreArr.x, ptCoinSupGauchePoutreArr.y+mmHauteurPoutre);
        }


        this.polygon = polyRetour;







/*
        parent.calculProfilElliptique();
        Polygon arcSupGauche = Ellipse.getArc1();
        int positionAffichage = positionRelative.getMillimetres();
        Rectangle poutreArriere = new Rectangle();
        int arcXMax = Ellipse.getPtCoinSupGaucheFin().x;
        Point pointPlusPres;
        int indexPointPlusPres = 0;
        Point p1;
        Point pointDroitRectangle;


        AffineTransform affine = new AffineTransform();

        if(positionRelative.getMillimetres() > arcXMax){

            position = new Point(positionRelative.getMillimetres(), 0);
            poutreArriere = new Rectangle(positionRelative.getMillimetres(), 0, largeur.getMillimetres(), hauteur.getMillimetres());

        }
        else{
            pointPlusPres = new Point(arcSupGauche.xpoints[0], arcSupGauche.ypoints[0]);

            for(int i = 0 ; i < arcSupGauche.npoints; i++){
                if(Math.abs(arcSupGauche.xpoints[i]-positionAffichage) < Math.abs(pointPlusPres.getX()-positionAffichage)){
                    pointPlusPres = new Point(arcSupGauche.xpoints[i], arcSupGauche.ypoints[i]);
                    indexPointPlusPres = i;
                }
            }
            position = pointPlusPres;
            poutreArriere = new Rectangle(pointPlusPres.x, pointPlusPres.y, largeur.getMillimetres(), hauteur.getMillimetres());


            Ellipse2D.Double cercleDeCalcul = new Ellipse2D.Double(pointPlusPres.x, pointPlusPres.y, largeur.getMillimetres()*2, largeur.getMillimetres()*2);
            Arc2D.Double arc = new Arc2D.Double(cercleDeCalcul.getBounds2D(), 0, 180, Arc2D.OPEN);
            PathIterator pathArc = arc.getPathIterator(new AffineTransform(),1);
            Polygon polygio = PlanRoulotteDessineur.toPolygon(pathArc);

            pointDroitRectangle = PlanRoulotte.trouvePointSurCourbe(Ellipse.getPtCoinSupGaucheFin(), new Point(Ellipse.getPtCoinSupGaucheFin().x + largeur.getMillimetres(), Ellipse.getPtCoinSupGaucheFin().y), pathArc);

            if(pointDroitRectangle == null){
                for(int i = indexPointPlusPres + 1; i < arcSupGauche.npoints; i++){

                    pointDroitRectangle = PlanRoulotte.trouvePointSurCourbe(new Point(arcSupGauche.xpoints[i-1], arcSupGauche.ypoints[i-1]), new Point(arcSupGauche.xpoints[i], arcSupGauche.ypoints[i]), pathArc);

                    if (pointDroitRectangle != null) {break;}
                }
            }


            //trouver l'angle

            assert pointDroitRectangle != null;
            double radian = Math.atan2(Math.abs(pointPlusPres.getY() - pointDroitRectangle.getY()), Math.abs(pointPlusPres.getX() - pointDroitRectangle.getX()));


            affine.setToRotation(radian,pointPlusPres.getX(),pointPlusPres.getY());

        }

        polygon = PlanRoulotteDessineur.toPolygon(poutreArriere.getPathIterator(affine));


 */
        dispositionValide = true;
    }

    @Override
    public int getPrioriteAffichage() {return this.prioriteAffichage;}

    @Override
    public boolean contient(Point point) {
        return this.getPolygon().contains(point);
    }

    @Override
    public TypeElement getType() {return TypeElement.POUTREARRIERE;}


}
