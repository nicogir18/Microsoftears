package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddOuvLatModeAction implements ActionListener {
    private final MenuPrincipal menuPrincipal;

    public AddOuvLatModeAction(MenuPrincipal menuPrincipal) {this.menuPrincipal = menuPrincipal;}

    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.setAppMode(MenuPrincipal.ApplicationMode.ADD_OUV_LAT);
    }
}
