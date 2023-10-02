package ca.ulaval.glo2004.domaine.Afficheur;

import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.ElementSelectionnable;
import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotteControleur;

import java.awt.*;
import java.awt.geom.*;

public class PlanRoulotteDessineur {
    private PlanRoulotteControleur controleur;
    private double zoomFacteur;

    public PlanRoulotteDessineur(PlanRoulotteControleur controleur, Double zoomFacteur) {
        this.controleur = controleur;
        this.zoomFacteur = zoomFacteur;
    }

    public void dessiner(Graphics2D g2D, ElementSelectionnable elementSelectionnable) {

        g2D.setColor(Color.BLACK);
        g2D.setColor(Color.BLACK);
        AffineTransform at = new AffineTransform();
        at.setToScale( zoomFacteur *0.5,  zoomFacteur *0.5);
        at.translate(2500,2500);

        PathIterator path = elementSelectionnable.getPolygon().getPathIterator(at);

        Polygon poly = toPolygon(path);

        g2D.draw(poly);
        if (elementSelectionnable.isSelected()) {
            g2D.setColor(new Color(0,0,255,99));
            g2D.fill(poly);
        }
    }

    public void dessinerGrille(Graphics2D g2d, int espacement) {
        double espacementDouble = espacement * 0.5 * zoomFacteur;
        double size = 5000 * zoomFacteur;
        double nombreCarresX = size / espacementDouble;
        double nombreCarresY = size / espacementDouble;

        // Dessin de la grille
        g2d.setColor(Color.LIGHT_GRAY);
        // Il faut mettre un nombre pour les incréments, donc ça veut dire que les carrées devront être à la même distance tout le temps (c'est ce que j'en comprends)
        for (int i=0;i<(int)nombreCarresX;i++) {
            g2d.draw(new Line2D.Double(i*espacementDouble,0,i*espacementDouble,size));
        }
        for (int i=0;i<(int)nombreCarresY;i++) {
            g2d.draw(new Line2D.Double(0,i*espacementDouble,size,i*espacementDouble));
        }
    }

    public void dessinerPanneauBrut(Graphics2D g2D, Polygon poly) {
        g2D.setColor(Color.LIGHT_GRAY);

        AffineTransform at = new AffineTransform();
        at.setToScale( zoomFacteur *0.5,  zoomFacteur *0.5);
        at.translate(2500,2500);

        PathIterator path = poly.getPathIterator(at);
        Polygon polyTemp = toPolygon(path);

        g2D.draw(polyTemp);
    }

    public static Polygon toPolygon(PathIterator pathIterator){
        float[] coordonnees = new float[6];
        Polygon polygon = new Polygon();


        while (!pathIterator.isDone()){
            int temoin = pathIterator.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            polygon.addPoint(x, y);
            pathIterator.next();
        }

        return polygon;
    }


}