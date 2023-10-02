package ca.ulaval.glo2004;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        MenuPrincipal MenuPrincipal = new MenuPrincipal();
        MenuPrincipal.setExtendedState(MenuPrincipal.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        MenuPrincipal.setVisible(true);
    }
}
