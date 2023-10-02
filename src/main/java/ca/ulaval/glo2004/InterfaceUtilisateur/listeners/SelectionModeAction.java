package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal.ApplicationMode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectionModeAction implements ActionListener {
    private final MenuPrincipal menuPrincipal;

    public SelectionModeAction(MenuPrincipal menuPrincipal) {this.menuPrincipal = menuPrincipal;}

    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.setAppMode(ApplicationMode.SELECT);
    }
}