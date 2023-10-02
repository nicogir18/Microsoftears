package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class DrawingPanelMouseWheel implements MouseWheelListener {
    private final MenuPrincipal menuPrincipal;

    public DrawingPanelMouseWheel(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        MenuPrincipal.ApplicationMode mode = menuPrincipal.getAppMode();
        if (mode == MenuPrincipal.ApplicationMode.SELECT){
            menuPrincipal.getDrawingPanel().updatePreferredSize(e.getWheelRotation(),e.getPoint());
        }
    }
}
