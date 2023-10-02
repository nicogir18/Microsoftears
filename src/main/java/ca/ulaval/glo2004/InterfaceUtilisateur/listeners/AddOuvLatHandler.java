package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddOuvLatHandler implements ActionListener {
    private final MenuPrincipal menuPrincipal;
    private final Mesure largeur;
    private final Mesure hauteur;
    private final JToggleButton select;

    public AddOuvLatHandler(MenuPrincipal menuPrincipal, Mesure largeur, Mesure hauteur, JToggleButton select) {
        this.menuPrincipal = menuPrincipal;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.select = select;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.controleur.triggerChangementImminent();
        menuPrincipal.notifierUndoRedoBoutons();
        menuPrincipal.controleur.addOuvLat(menuPrincipal.getPointAjout(), largeur, hauteur);
        select.doClick();
        menuPrincipal.repaint();
    }
}
