package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.DrawingPanel;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.Table.*;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.*;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.ElementSelectionnable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Objects;

public class DrawingPanelMouse implements MouseListener {
    private final MenuPrincipal menuPrincipal;
    private MenuPrincipal.ApplicationMode mode;

    public DrawingPanelMouse(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        mode = menuPrincipal.getAppMode();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mode = menuPrincipal.getAppMode();
        switch (mode) {
            case SELECT:
                break;
            case ADD_AIDE_DES:
                break;
            case ADD_OUV_LAT:
                break;
            case DELETE:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point mousePoint = e.getPoint();

        mode = menuPrincipal.getAppMode();

        if (mode == MenuPrincipal.ApplicationMode.DELETE){}


        else if (mode == MenuPrincipal.ApplicationMode.ADD_AIDE_DES) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                menuPrincipal.showAddAideMenu(e);
                menuPrincipal.setPointAjout(new Point2D.Double(e.getX(), e.getY()));
            }
        }
        else if (mode == MenuPrincipal.ApplicationMode.ADD_OUV_LAT) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                menuPrincipal.showAddOuvMenu(e);
                menuPrincipal.setPointAjout(new Point2D.Double(e.getX(), e.getY()));
            }
        }
        else if (mode == MenuPrincipal.ApplicationMode.EXPORT_PANNEAU){
            if (SwingUtilities.isLeftMouseButton(e)) {
                menuPrincipal.showExportPanneauMenu(e);
                menuPrincipal.setPointAjout(new Point2D.Double(e.getX(), e.getY()));
            }
        }
        else if (mode == MenuPrincipal.ApplicationMode.SELECT && SwingUtilities.isLeftMouseButton(e)){

            Dimension initial = menuPrincipal.getDrawingPanel().getInitialDimension();
            Dimension preferred = menuPrincipal.getDrawingPanel().getPreferredSize();
            double factor = preferred.getWidth() / initial.getWidth();
            // Transformation du point en fonction du zoom
            Point2D.Double newPoint = new Point2D.Double(mousePoint.x / factor, mousePoint.y / factor);
            Point pointTransforme = new Point((int)newPoint.x*2-2500,(int)newPoint.y*2-2500);

            JScrollPane pane = (JScrollPane) menuPrincipal.propertiesPane.getComponent(0);
            JViewport port = pane.getViewport();
            JTable table = (JTable) port.getView();

            menuPrincipal.controleur.alternerStatusSelection(pointTransforme.x, pointTransforme.y);

            ElementSelectionnable element = menuPrincipal.controleur.whatObjectIsSelected();
            if (element == null)
                table.setModel(new DefaultTableModel());
            else
                table.setModel(new ElementTable(element.getElementDTO(), menuPrincipal));
        }
        menuPrincipal.getDrawingPanel().setOrigin(new Point2D.Double(mousePoint.x,mousePoint.y));
        menuPrincipal.getDrawingPanel().repaint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        menuPrincipal.getDrawingPanel().setOrigin(new Point2D.Double(e.getX(),e.getY()));
        if (menuPrincipal.getAppMode() == MenuPrincipal.ApplicationMode.SELECT) {
            if (SwingUtilities.isRightMouseButton(e)) {
                menuPrincipal.controleur.setTranslating(false);
            }
        }
        menuPrincipal.getDrawingPanel().repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
