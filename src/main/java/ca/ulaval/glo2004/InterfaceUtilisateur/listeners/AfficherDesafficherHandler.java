package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.ElementSelectionnable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AfficherDesafficherHandler implements ActionListener {

    private MenuPrincipal menuPrincipal;
    private ElementSelectionnable.TypeElement type;

    public AfficherDesafficherHandler(MenuPrincipal menuPrincipal, ElementSelectionnable.TypeElement type) {
        this.menuPrincipal = menuPrincipal;
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.controleur.setElementVisibilite(type);
        menuPrincipal.repaint();
    }
}
