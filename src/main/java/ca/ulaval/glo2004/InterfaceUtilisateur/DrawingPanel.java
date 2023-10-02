package ca.ulaval.glo2004.InterfaceUtilisateur;

import ca.ulaval.glo2004.domaine.Afficheur.PlanRoulotteDessineur;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.Ellipse;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DrawingPanel extends JPanel {

    private static int prevN = 0;
    private Dimension minimumSize = new Dimension(50,50);
    private Dimension maximumSize = new Dimension(10000,10000);
    private Dimension preferredSize = new Dimension(5000, 5000);
    private final Dimension initialDimension = new Dimension(5000,5000);
    private double ancienFacteurZoom = 1.00;

    private int deplacementX = 0;
    private int deplacementY = 0;

    private MenuPrincipal menuPrincipal;
    private boolean EstQuadrilleActif;
    private double zoom;
    private Point2D.Double origin = new Point2D.Double(0,0);

    private boolean firstPaint = true;

    public DrawingPanel(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        this.EstQuadrilleActif = false;
        zoom = 1;
    }

    // FONCTION DE ZOOM
    public void updatePreferredSize(int n, Point p) {
        if (n == 0) {
            n = -1 * prevN;
        }
        double d = (double) n * 1.08;
        d = (n > 0) ? 1 / d : -d;

        origin = new Point2D.Double(origin.x * d, origin.y * d);

        int w = (int) (getWidth() * d);
        int h = (int) (getHeight() * d);
        preferredSize.setSize(w, h);

        int offX = (int)(p.x * d) - p.x;
        int offY = (int)(p.y * d) - p.y;

        deplacementX += getLocation().x-offX;
        deplacementY += getLocation().y-offY;

        setLocation(getLocation().x-offX,getLocation().y-offY);

        menuPrincipal.controleur.setFacteurZoom(preferredSize.getWidth() / initialDimension.getWidth());
        ancienFacteurZoom = getWidth() / initialDimension.getWidth();

        doLayout();
        getParent().doLayout();

        prevN = n;
    }

    public Dimension getInitialDimension() {return this.initialDimension;}

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    @Override
    public Dimension getMaximumSize() {
        return maximumSize;
    }

    @Override
    public Dimension getMinimumSize() {
        return minimumSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (menuPrincipal != null){
            Graphics2D g2D = (Graphics2D) g;
            super.paintComponent(g2D);

            int w = getWidth();
            int h = getHeight();
            // Garder le ratio 1:1
            if (w > h) {
                preferredSize.setSize(w,w);
            }
            if (h > w) {
                preferredSize.setSize(h,h);
            }

            PlanRoulotteDessineur mainDrawer = menuPrincipal.controleur.createPlanRoulotteDessineur(g2D);


            if (ancienFacteurZoom != menuPrincipal.controleur.getFacteurZoom()) {
                menuPrincipal.updateZoom(menuPrincipal.controleur.getFacteurZoom());
            }
            menuPrincipal.updateUnite(menuPrincipal.controleur.getEstImperiale());
            menuPrincipal.refreshGrille();

            if (firstPaint) {
                repaint();
                firstPaint = false;
            }

        }
    }

    public MenuPrincipal getMenuPrincipal() {
        return menuPrincipal;
    }

    public void setOrigin(Point2D.Double origin) {
        this.origin = origin;
    }

    public Point2D.Double getOrigin() {
        return origin;
    }

}
