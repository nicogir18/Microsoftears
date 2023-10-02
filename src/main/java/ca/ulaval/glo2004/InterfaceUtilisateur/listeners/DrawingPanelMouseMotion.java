package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.DrawingPanel;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.Table.ElementTable;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.ElementSelectionnable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

public class DrawingPanelMouseMotion implements MouseMotionListener {
    private final MenuPrincipal menuPrincipal;
    private MenuPrincipal.ApplicationMode mode;

    public DrawingPanelMouseMotion(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        mode = menuPrincipal.getAppMode();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mode = menuPrincipal.getAppMode();
        switch (mode) {
            case SELECT:
                DrawingPanel drawingPanel = menuPrincipal.getDrawingPanel();
                Point2D.Double origin = drawingPanel.getOrigin();
                //menuPrincipal.getDrawingPanel().setOrigin(new Point2D.Double(e.getX(),e.getY()));

                if (SwingUtilities.isLeftMouseButton(e)) {
                    Rectangle view = drawingPanel.getVisibleRect();
                    view.x += origin.x - e.getX();
                    view.y += origin.y - e.getY();
                    drawingPanel.scrollRectToVisible(view);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    // gestion undo redo
                    boolean isTranslating = menuPrincipal.controleur.isTranslating();
                    if (!isTranslating) {
                        menuPrincipal.controleur.triggerChangementImminent();
                        menuPrincipal.controleur.setTranslating(true);
                        menuPrincipal.notifierUndoRedoBoutons();
                    }
                    drawingPanel.setOrigin(new Point2D.Double(e.getX(),e.getY()));
                    double deltaX = e.getX() - origin.x;
                    double deltaY = e.getY() - origin.y;

                    Dimension initial = menuPrincipal.getDrawingPanel().getInitialDimension();
                    Dimension preferred = menuPrincipal.getDrawingPanel().getPreferredSize();
                    double factor = preferred.getWidth() / initial.getWidth();

                    Point2D.Double newPoint = new Point2D.Double((deltaX/factor)*2, (deltaY/factor)*2);

                    menuPrincipal.controleur.miseAJourPositionElementSelectionne(newPoint);
                    menuPrincipal.getDrawingPanel().repaint();

                    JScrollPane pane = (JScrollPane) menuPrincipal.propertiesPane.getComponent(0);
                    JViewport port = pane.getViewport();
                    JTable table = (JTable) port.getView();

                    ElementSelectionnable element = menuPrincipal.controleur.whatObjectIsSelected();

                    if (element == null)
                        table.setModel(new DefaultTableModel());
                    else
                        table.setModel(new ElementTable(element.getElementDTO(), menuPrincipal));


                }
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
    public void mouseMoved(MouseEvent e) {
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
}
