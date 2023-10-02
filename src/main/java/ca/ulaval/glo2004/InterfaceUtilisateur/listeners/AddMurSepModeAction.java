package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal.ApplicationMode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMurSepModeAction implements ActionListener {
    private final MenuPrincipal menuPrincipal;

    public AddMurSepModeAction(MenuPrincipal menuPrincipal) {this.menuPrincipal = menuPrincipal;}

    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.controleur.triggerChangementImminent();
        menuPrincipal.notifierUndoRedoBoutons();
        menuPrincipal.controleur.ajoutMurSeparateur();
        menuPrincipal.repaint();
    }
}
