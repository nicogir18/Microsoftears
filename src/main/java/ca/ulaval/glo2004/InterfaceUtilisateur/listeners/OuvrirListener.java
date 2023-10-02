package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OuvrirListener implements ActionListener {
    private final MenuPrincipal menuPrincipal;

    public OuvrirListener(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser c = new JFileChooser();
        String fileName = "";
        int rVal = c.showOpenDialog(menuPrincipal);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            fileName = c.getSelectedFile().toString();
            menuPrincipal.controleur.charger(fileName);
            menuPrincipal.repaint();
        }
        else{

        }
    }
}
