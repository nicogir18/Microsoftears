package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal.ApplicationMode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteModeAction implements ActionListener {
    private final MenuPrincipal menuPrincipal;
    private final JToggleButton select;

    public DeleteModeAction(MenuPrincipal menuPrincipal, JToggleButton select) {
        this.menuPrincipal = menuPrincipal;
        this.select = select;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        menuPrincipal.setAppMode(ApplicationMode.DELETE);
        menuPrincipal.controleur.triggerChangementImminent();
        menuPrincipal.notifierUndoRedoBoutons();
        menuPrincipal.controleur.supprimeElementSelectionne();
        select.doClick();
        menuPrincipal.repaint();
    }
}
