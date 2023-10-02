package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.Table.*;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOEllipse;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOHayon;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPanneau;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOPlancher;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.ElementSelectionnable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UniteHandler implements ActionListener {

    private final MenuPrincipal menuPrincipal;

    public UniteHandler(MenuPrincipal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JRadioButton bouton = (JRadioButton) e.getSource();
        String nom = bouton.getText();
        boolean isImperial = nom.equals("Impérial");
        menuPrincipal.controleur.setEstImperiale(isImperial);

        menuPrincipal.refreshGrille();

        JTable table = (JTable) menuPrincipal.propertiesPane.getComponent(0);
        ElementSelectionnable element = menuPrincipal.controleur.whatObjectIsSelected();

        if (element == null)
            table.setModel(new DefaultTableModel());
        else
            table.setModel(new ElementTable(element.getElementDTO(), menuPrincipal));

    }
}
