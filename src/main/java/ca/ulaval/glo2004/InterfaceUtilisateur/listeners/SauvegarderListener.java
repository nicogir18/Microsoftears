package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SauvegarderListener implements ActionListener {
    private final MenuPrincipal menuPrincipal;
    private final boolean isEnregistrerSous;
    public SauvegarderListener(MenuPrincipal menuPrincipal, boolean isEnregistrerSous) {
        this.isEnregistrerSous = isEnregistrerSous;
        this.menuPrincipal = menuPrincipal;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(menuPrincipal.controleur.getCheminEnregistrement() == null || isEnregistrerSous) {
            JFileChooser c = new JFileChooser();
            String fileName = "";
            int rVal = c.showOpenDialog(menuPrincipal);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                fileName = c.getSelectedFile().toString();
                menuPrincipal.controleur.sauvegarder(fileName);
            }
        } else {
            menuPrincipal.controleur.sauvegarder(menuPrincipal.controleur.getCheminEnregistrement());
        }
    }
}
