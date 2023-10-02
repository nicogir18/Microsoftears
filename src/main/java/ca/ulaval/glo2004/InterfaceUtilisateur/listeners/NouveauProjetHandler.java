package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.domaine.planRoulotte.PlanRoulotteControleur;
import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NouveauProjetHandler implements ActionListener {
    private final MenuPrincipal menuPrincipal;
    private final boolean estEllipse;

    public NouveauProjetHandler(MenuPrincipal menuPrincipal, boolean estEllipse) {
        this.estEllipse = estEllipse;
        this.menuPrincipal = menuPrincipal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        menuPrincipal.controleur = new PlanRoulotteControleur(estEllipse);
        menuPrincipal.notifierUndoRedoBoutons();
        menuPrincipal.repaint();
    }
}
