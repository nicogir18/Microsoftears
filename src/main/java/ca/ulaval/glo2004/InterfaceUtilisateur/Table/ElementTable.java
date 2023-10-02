package ca.ulaval.glo2004.InterfaceUtilisateur.Table;

import ca.ulaval.glo2004.InterfaceUtilisateur.MenuPrincipal;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOElement;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.DTOEllipse;
import ca.ulaval.glo2004.domaine.planRoulotte.Mesure;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class ElementTable extends AbstractTableModel implements TableModelListener {
    private String[][] data;
    private String[] colonnes;
    private MenuPrincipal menuPrincipal;

    public ElementTable(DTOElement element, MenuPrincipal menuPrincipal) {
        this.data = element.getListeVariables(menuPrincipal.controleur.getEstImperiale());
        this.colonnes = new String[]{"Propriété - " + element.getNom(),"Valeur"};
        this.menuPrincipal = menuPrincipal;
        addTableModelListener(this);
    }

    @Override
    public boolean isCellEditable(int row, int column) {return column !=0;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int column) {
        return colonnes[column];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = String.valueOf(aValue);
        fireTableCellUpdated(rowIndex,columnIndex);
        //fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return colonnes.length;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        ArrayList<Mesure> mesures = new ArrayList<Mesure>();
        double poids = 0;
        UUID id;
        for (int i = 0; i < data.length; i++){
            String[] mesuresString = getValueAt(i,1).toString().split(" ");
            int num = 0, deno = 0;

            if (mesuresString.length != 1 ){
                String[] fraction = mesuresString[1].split("/");
                if (!stringEstNombre(fraction[0]) && !stringEstNombre(fraction[1]))
                    break;
                num = Integer.parseInt(fraction[0]);
                deno = Integer.parseInt(fraction[1]);
            }
            if (getValueAt(i,0).toString() != "Poids") {
                if (!stringEstNombre(mesuresString[0]))
                    break;
                if (menuPrincipal.controleur.getEstImperiale())
                    mesures.add(new Mesure(Integer.parseInt(mesuresString[0]), num, deno));
                else {
                    Mesure tempMesure = new Mesure(0,0,0);
                    tempMesure.setMetrique(Integer.parseInt(mesuresString[0]));
                    mesures.add(tempMesure);
                }
            }
            else{
                if (!stringEstDouble(getValueAt(i,1).toString()))
                    break;
                poids = Double.parseDouble(getValueAt(i,1).toString());
            }
        }

        if (mesures.size() > 0){
            menuPrincipal.controleur.triggerChangementImminent();
            menuPrincipal.notifierUndoRedoBoutons();
            if (menuPrincipal.controleur.objetSelection().getElementDTO() instanceof DTOEllipse){
                id = ((DTOEllipse) menuPrincipal.controleur.objetSelection().getElementDTO()).getId();
                menuPrincipal.controleur.changeDimElement(id, mesures);
            }
            else
                menuPrincipal.controleur.changeDimElement(poids, mesures);
        }

        menuPrincipal.getDrawingPanel().repaint();
        menuPrincipal.getDrawingPanel().requestFocusInWindow();
    }

    static private boolean stringEstNombre(String fraction){
        try {
            Integer.parseInt(fraction);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    static private boolean stringEstDouble(String nombre) {
        try {
            Double.parseDouble(nombre);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
