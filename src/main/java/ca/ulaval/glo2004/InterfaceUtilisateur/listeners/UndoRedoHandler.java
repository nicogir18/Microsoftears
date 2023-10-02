package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


// TODO: Ajouter au UML
public class UndoRedoHandler implements ActionListener {
    private final MenuPrincipal menuPrincipal;
    private boolean isUndo;
    private final JButton bouton;

    public UndoRedoHandler(MenuPrincipal menuPrincipal, boolean isUndo, JButton bouton) {
        this.menuPrincipal = menuPrincipal;
        this.isUndo = isUndo;
        this.bouton = bouton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean actionRealisable;
        if (isUndo) {
            actionRealisable = menuPrincipal.controleur.undo();
        } else {
            actionRealisable = menuPrincipal.controleur.redo();
        }
        bouton.setEnabled(actionRealisable);
        menuPrincipal.repaint();
    }

    public void refreshState() {
        boolean actionRealisable = menuPrincipal.controleur.isUndoRedoAvailable(isUndo);
        bouton.setEnabled(actionRealisable);
    }
}
