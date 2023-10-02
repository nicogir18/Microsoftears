package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.InterfaceUtilisateur.Table.ElementTable;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GrilleEspacementListener implements ActionListener {
    private MenuPrincipal menuPrincipal;
    private JTextField textField;

    public GrilleEspacementListener(MenuPrincipal menuPrincipal, JTextField textField) {
        this.menuPrincipal = menuPrincipal;
        this.textField = textField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String texte = textField.getText();

        if (menuPrincipal.controleur.getEstImperiale()) {
            int fraction = 0;
            int numerateur = 0;
            int denominateur = 0;
            String[] mesuresString = texte.split(" ");
            if (mesuresString.length != 1) {
                String[] fractions = mesuresString[1].split("/");
                if (stringEstNombre(fractions[0]) && stringEstNombre(fractions[1]) && stringEstNombre(mesuresString[0])) {
                    fraction = Integer.parseInt(mesuresString[0]);
                    numerateur = Integer.parseInt(fractions[0]);
                    denominateur = Integer.parseInt(fractions[1]);

                    Mesure espacement = new Mesure(fraction, numerateur, denominateur);
                    menuPrincipal.controleur.setEspacementGrille(espacement);
                }
            }
            else {
                Mesure espacement = new Mesure(Integer.parseInt(mesuresString[0]), 0, 0);
                menuPrincipal.controleur.setEspacementGrille(espacement);
            }
        } else {
            if (stringEstNombre(texte)) {
                int millimetres = Integer.parseInt(texte);
                Mesure espacement = new Mesure(0,0,0);
                espacement.setMetrique(millimetres);
                menuPrincipal.controleur.setEspacementGrille(espacement);
            }
        }
        loadData();
        menuPrincipal.requestFocus();
        menuPrincipal.repaint();
    }

    static private boolean stringEstNombre(String fraction){
        try {
            Integer.parseInt(fraction);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void loadData() {
        boolean estImperial = menuPrincipal.controleur.getEstImperiale();
        Mesure espacement = menuPrincipal.controleur.getEspacementGrille();
        String texte;
        if (estImperial) {
            texte = espacement.getPoucesEntier() + " " + espacement.getPoucesFractionsNumerateur() + "/" + espacement.getPoucesFractionsDenominateur();
        } else {
            texte = Integer.toString(espacement.getMillimetres());
        }
        textField.setText(texte);
    }
}
