package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.DrawingPanel;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GrilleHandler implements ActionListener {
    private final MenuPrincipal menuPrincipal;

    public GrilleHandler(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.toggleGrille();
        menuPrincipal.controleur.setGrille();
        menuPrincipal.repaint();
    }
}
