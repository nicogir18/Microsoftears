package ca.ulaval.glo2004.InterfaceUtilisateur.listeners;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.Panneau;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExportListener implements ActionListener {

    private final MenuPrincipal menuPrincipal;
    private final boolean isInterne;
    private boolean isJPG;

    public ExportListener(MenuPrincipal menuPrincipal, boolean isInterne, boolean isJPG){
        this.menuPrincipal = menuPrincipal;
        this.isInterne = isInterne;

        if (menuPrincipal.getAppMode() != MenuPrincipal.ApplicationMode.EXPORT_PANNEAU){
            this.isJPG = isJPG;
            this.menuPrincipal.controleur.setIsExportJPG(isJPG);
        } else {
            this.isJPG = this.menuPrincipal.controleur.isExportJPG();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (menuPrincipal.controleur.objetSelection() instanceof Panneau){
            if (menuPrincipal.getAppMode() == MenuPrincipal.ApplicationMode.EXPORT_PANNEAU)
            {
                JFileChooser c = new JFileChooser();
                String fileName = "";
                int rVal = c.showOpenDialog(menuPrincipal);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    fileName = c.getSelectedFile().toString();
                }
                if (this.menuPrincipal.controleur.isExportJPG())
                    menuPrincipal.controleur.exportePlanJPG(isInterne, fileName);
                else
                    menuPrincipal.controleur.exportePlanSVG(isInterne, fileName);
                menuPrincipal.repaint();
            }

        }
        else{
            if (((JMenuItem)e.getSource()).getName() == "JPG") {
                this.menuPrincipal.controleur.setIsExportJPG(true);
                this.isJPG = true;
            }
            else{
                this.menuPrincipal.controleur.setIsExportJPG(false);
                this.isJPG = false;
            }

            JFileChooser c = new JFileChooser();
            String fileName = "";
            int rVal = c.showOpenDialog(menuPrincipal);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                fileName = c.getSelectedFile().toString();
            }
            if (isJPG)
                menuPrincipal.controleur.exportePlanJPG(isInterne, fileName);
            else
                menuPrincipal.controleur.exportePlanSVG(isInterne, fileName);
        }

        if (menuPrincipal.controleur.objetSelection() instanceof Panneau){
            if (menuPrincipal.getAppMode() == MenuPrincipal.ApplicationMode.EXPORT_PANNEAU) {
                menuPrincipal.setAppMode(MenuPrincipal.ApplicationMode.SELECT);
            } else {
                if (((JMenuItem)e.getSource()).getName() == "JPG") {
                    this.menuPrincipal.controleur.setIsExportJPG(true);
                    this.isJPG = true;
                }
                else{
                    this.menuPrincipal.controleur.setIsExportJPG(false);
                    this.isJPG = false;
                }
                menuPrincipal.setAppMode(MenuPrincipal.ApplicationMode.EXPORT_PANNEAU);
            }
        }
        menuPrincipal.repaint();
    }
}
