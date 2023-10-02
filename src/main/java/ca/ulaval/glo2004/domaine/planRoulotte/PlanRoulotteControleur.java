package ca.ulaval.glo2004.domaine.planRoulotte;

import ca.ulaval.glo2004.domaine.Afficheur.PlanRoulotteDessineur;
import ca.ulaval.glo2004.domaine.planRoulotte.Factory.*;
import ca.ulaval.glo2004.domaine.planRoulotte.Modeles.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PlanRoulotteControleur {

    private PlanRoulotte planRoulotte;
    private double facteurZoom = 1;
    private String cheminEnregistreSous = null;
    // TODO: Nouvelles varsiables (les 4 en dessous)
    private java.util.List<PlanRoulotte> listeStates = new ArrayList<>();
    private int stateIndex = 0;
    private File tempFile = null;
    private boolean translating = false;

    public PlanRoulotteControleur(boolean estEllipse) {

        this.planRoulotte = new PlanRoulotte(estEllipse);
        cheminEnregistreSous = null;
        listeStates = new ArrayList<>();
        tempFile = null;
    }

    public PlanRoulotteControleur(PlanRoulotte planRoulotte) {
        this.planRoulotte = planRoulotte;
    }

    public void ajoutMurSeparateur() {
        planRoulotte.ajouteMurSeparateur();
    }

    public void ajoutAideAuDesign(Point mousePoint) {}

    public void ajoutOuvertureLaterale(Point mousePoint) {}

    public void configureRessortAGaz() {}

    public DTOPanneau getDTOPanneau(){return planRoulotte.getDTOPanneau();}

    public boolean isExportJPG(){
        return planRoulotte.isExportJPG();
    }

    public void setIsExportJPG(boolean isExportJPG){
        planRoulotte.setExportType(isExportJPG);
    }
    public PlanRoulotteDessineur createPlanRoulotteDessineur(Graphics2D g){
        PlanRoulotteDessineur planRoulotteDessineur = new PlanRoulotteDessineur(this, facteurZoom);
        if (planRoulotte.getGrille().isActive()) {
            planRoulotteDessineur.dessinerGrille(g, planRoulotte.getGrille().getEspacement().getMillimetres());
        }
        if (planRoulotte.getProfilType() == PlanRoulotte.PROFIL_TYPE.BEZIER) {
            planRoulotteDessineur.dessinerPanneauBrut(g, planRoulotte.getPolyPanneauBrut());
        }
       for (ElementSelectionnable elementSelectionnable : planRoulotte.getElementsSelectionnables()){
            if (elementSelectionnable.estVisible()) {
                planRoulotteDessineur.dessiner(g,elementSelectionnable);
            }
        }
        return planRoulotteDessineur;
    }

    public void changeDimPanneau(Mesure hauteur, Mesure largeur){
        planRoulotte.changeDimPanneau(hauteur, largeur);
    }

    public void changeDimPoutreArriere(Mesure hauteur, Mesure largeur, Mesure positionRelative){
        planRoulotte.changeDimPoutreArriere(hauteur, largeur, positionRelative);
    }

    public void changeDimPlancher(Mesure epaisseur, Mesure margeAvant, Mesure margeArriere){
        planRoulotte.changeDimPlancher(epaisseur, margeAvant, margeArriere);
    }

    //TODO: utiliser planroulotte
    /**
     *
     * @param autre Utilisé pour toute valeur qui n'est pas de type mesure
     * @param mesures IMPORTANT DE SUIVRE L'ORDRE DE TOUTES LES MESURES
     * Panneau: hauteur, largeur
     * Ellipse: hauteur, largeur, x , y, id (utiliser param autre)
     * Hayon: epaisseur, distanceDePoutre, distanceDuPlancher, traitDeScie, rayonDeCourbure, poids (utiliser param autre)
     * Plancher: epaisseur, margeAvant, margeArriere
     * @param <T>
     */
    public <T> void changeDimElement(T autre, ArrayList<Mesure> mesures) {

        ElementSelectionnable.TypeElement elementType = whatElementIsSelected();
        switch(elementType){
            case PANNEAU:
                changeDimPanneau(mesures.get(1), mesures.get(0));
                break;
            case ELLIPSE:
                changeDimEllipse(mesures.get(0), mesures.get(1), mesures.get(2), mesures.get(3));
                break;
            case HAYON:
                planRoulotte.changeDimHayon(mesures.get(0), mesures.get(1), mesures.get(2), mesures.get(3), mesures.get(4), (Double) autre);
                break;
            case PLANCHER:
                planRoulotte.changeDimPlancher(mesures.get(0), mesures.get(1), mesures.get(2));
                break;
            case POUTREARRIERE:
                planRoulotte.changeDimPoutreArriere(mesures.get(1), mesures.get(0), mesures.get(2));
                break;
            case MURSEPARATEUR:
                planRoulotte.changeDimMurSeparateur(mesures.get(0), mesures.get(1), mesures.get(2));
                break;
            case TOIT:
                planRoulotte.changeDimToit(mesures.get(0));
                break;
            case AIDEAUDESIGN:
                planRoulotte.changeDimAideAuDesign(mesures.get(0), mesures.get(1), mesures.get(2), mesures.get(3), (AideAuDesign) objetSelection());
                break;
            case OUVERTURELATERALE:
                planRoulotte.changeDimOuvLat(mesures.get(0), mesures.get(1), mesures.get(2), mesures.get(3), (OuvertureLaterale) objetSelection());
                break;
        }
    }

    public void miseAJourPositionElementSelectionne(Point2D.Double delta){
        planRoulotte.miseAJourPositionElementSelectionne(delta);
    }

    public void supprimeElementSelectionne() {
        ElementSelectionnable elementSelectionne = objetSelection();
        planRoulotte.supprimeElementSelectionne(elementSelectionne);
    }

    public void addAideAuDesign(Point2D.Double pointInitial, Mesure largeur, Mesure hauteur) {
        planRoulotte.ajouteAideAuDesign(pointInitial, largeur, hauteur);
    }
    public void addOuvLat(Point2D.Double pointInitial, Mesure largeur, Mesure hauteur) {
        planRoulotte.ajouteOuvertureLaterale(pointInitial, largeur, hauteur);
    }


    public void exportePlanJPG(boolean typePanneau, String fileName) {
        BufferedImage bufferedImage;
        if (objetSelection() != null)
            if (typePanneau){
                bufferedImage = drawInterne();
            }
            else {
                bufferedImage = drawElement(objetSelection());
            }
        else
            bufferedImage = drawPolygone(planRoulotte.getContour());

        try {
            Path path = Paths.get(fileName).normalize();
            String realPath = enleverExtensionDeCheminDeFichier(path.toString());
            ImageIO.write(bufferedImage, "png", new File(realPath + ".jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    public void exportePlanSVG(boolean typePanneau, String fileName) {
        String svg;
        if (objetSelection() != null)
            if (typePanneau)
                svg = drawInterneSVG();
            else
            {
                if (objetSelection() instanceof Panneau){
                    svg = drawMurExterneSVG(objetSelection());
                } else {
                    svg = drawSVG(objetSelection());
                }
            }

        else
            svg = drawContourSVG();
        try {
            Path path = Paths.get(fileName).normalize();
            String realPath = enleverExtensionDeCheminDeFichier(path.toString());
            FileWriter myWriter = new FileWriter(realPath + ".svg");
            myWriter.write(svg);
            myWriter.close();
        } catch (IOException e){
            System.out.println("erreur");
            e.printStackTrace();
        }
    }
    public static String enleverExtensionDeCheminDeFichier(String fileName) {
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }

    }
    private BufferedImage drawElement(ElementSelectionnable elementSelectionnable) {
        AffineTransform at = new AffineTransform();
        at.setToScale(0.5,  0.5);
        PathIterator path = elementSelectionnable.getPolygon().getPathIterator(at);

        Polygon poly = toPolygon(path);
        return drawPolygone(poly);
    }
    private String drawSVG(ElementSelectionnable elementSelectionnable){
        AffineTransform at = new AffineTransform();
        at.setToScale(0.5,  0.5);
        float[] coordonnees = new float[6];
        String svg = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" height=\"1080\" width=\"1920\"> \n <polygon points=\"";
        PathIterator path = elementSelectionnable.getPolygon().getPathIterator(at);

        while (!path.isDone()){
            int temoin = path.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            svg += String.valueOf(x) + "," + String.valueOf(y) + " ";
            path.next();
        }
        svg += "\" style=\"stroke-width:1\"/> \n </svg>";
        return svg;
    }

    private String drawMurExterneSVG(ElementSelectionnable elementSelectionnable){
        AffineTransform at = new AffineTransform();
        at.setToScale(0.25,  0.25);
        float[] coordonnees = new float[6];
        String svg = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" height=\"1080\" width=\"1920\"> \n <polygon points=\"";
        String svgElements = "";
        PathIterator path = elementSelectionnable.getPolygon().getPathIterator(at);

        while (!path.isDone()){
            int temoin = path.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            svg += String.valueOf(x) + "," + String.valueOf(y) + " ";
            path.next();
        }

        for (ElementSelectionnable elem: planRoulotte.getElementsSelectionnablesExportable()
        ) {
            if (!elem.estExterne()){
                String tempSVG  = "<polygon points=\"";
                PathIterator pathElements = elem.getPolygon().getPathIterator(at);
                while (!pathElements.isDone()){
                    int temoin = pathElements.currentSegment(coordonnees);
                    int x = (int) coordonnees [0];
                    int y = (int) coordonnees [1];
                    tempSVG += String.valueOf(x) + "," + String.valueOf(y) + " ";
                    pathElements.next();
                }
                tempSVG += "\" style=\"fill:rgb(255,255,255);stroke:rgb(255,255,255)\"/> \n";
                svgElements += tempSVG;
            }
        }

        svg += "\" style=\"stroke-width:1\"/>" + svgElements + "\n </svg>";
        return svg;
    }
    private BufferedImage drawInterne() {
        AffineTransform at = new AffineTransform();

        final BufferedImage image = new BufferedImage((int)planRoulotte.getContour().getBounds().getWidth() +5,
                (int)planRoulotte.getContour().getBounds().getHeight() +5, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = image.createGraphics ();

        graphics2D.setPaint ( Color.WHITE );
        graphics2D.fillRect ( 0, 0, image.getWidth(), image.getHeight() );

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fill(planRoulotte.getContour());
        for (ElementSelectionnable elem: planRoulotte.getElementsSelectionnablesExportable()
             ) {
            if (!elem.estInterne()){
                PathIterator path = elem.getPolygon().getPathIterator(at);
                Polygon poly = toPolygon(path);
                graphics2D.setPaint ( Color.WHITE );
                graphics2D.fill(poly);
            }
        }
        graphics2D.dispose();

        return image;
    }

    private String drawContourSVG(){
        AffineTransform at = new AffineTransform();
        at.scale(0.5,0.5);
        float[] coordonnees = new float[6];
        String svg = "<svg height=\"1080\" width=\"1920\"> \n <polygon points=\"";
        PathIterator contourPath = planRoulotte.getContour().getPathIterator(at);
        while (!contourPath.isDone()){
            int t = contourPath.currentSegment(coordonnees);
            int xt = (int) coordonnees[0];
            int yt = (int) coordonnees[1];
            svg += String.valueOf(xt) + "," + String.valueOf(yt) + " ";
            contourPath.next();
        }
        svg += "\" style=\"stroke-width:1\"/> \n </svg>";
        return svg;
    }

    private String drawInterneSVG() {
        AffineTransform at = new AffineTransform();
        at.scale(0.5,0.5);
        float[] coordonnees = new float[6];
        String svg = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" height=\"1080\" width=\"1920\"> \n <polygon points=\"";
        String svgElements = "";
        PathIterator contourPath = planRoulotte.getContour().getPathIterator(at);
        while (!contourPath.isDone()){
            int t = contourPath.currentSegment(coordonnees);
            int xt = (int) coordonnees[0];
            int yt = (int) coordonnees[1];
            svg += String.valueOf(xt) + "," + String.valueOf(yt) + " ";
            contourPath.next();
        }
        for (ElementSelectionnable elem: planRoulotte.getElementsSelectionnablesExportable()
        ) {
            if (!elem.estInterne()){
                String tempSVG  = "<polygon points=\"";
                PathIterator path = elem.getPolygon().getPathIterator(at);
                while (!path.isDone()){
                    int temoin = path.currentSegment(coordonnees);
                    int x = (int) coordonnees [0];
                    int y = (int) coordonnees [1];
                    tempSVG += String.valueOf(x) + "," + String.valueOf(y) + " ";
                    path.next();
                }
                tempSVG += "\" style=\"fill:rgb(255,255,255);stroke:rgb(255,255,255)\"/> \n";
                svgElements += tempSVG;
            }
        }
        svg += "\" style=\"stroke-width:1\"/>" + svgElements + "\n </svg>";

        return svg;
    }

    private BufferedImage drawPolygone(Polygon polygon) {
        final BufferedImage image = new BufferedImage((int)polygon.getBounds().getWidth() +5,
                (int)polygon.getBounds().getHeight() +5, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = image.createGraphics ();

        graphics2D.setPaint ( Color.WHITE );
        graphics2D.fillRect ( 0, 0, image.getWidth(), image.getHeight() );

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fill(polygon);
        graphics2D.dispose();

        return image;
    }


    public static Polygon toPolygon(PathIterator pathIterator){
        float[] coordonnees = new float[6];
        Polygon polygon = new Polygon();


        while (!pathIterator.isDone()){
            int temoin = pathIterator.currentSegment(coordonnees);
            int x = (int) coordonnees [0];
            int y = (int) coordonnees [1];
            polygon.addPoint(x, y);
            pathIterator.next();
        }

        return polygon;
    }

    public void alternerStatusSelection(int x, int y) {
        planRoulotte.alternerSelectionPourCoordonnee(x,y);
    }

    public void selectionObjet(Point point) {
        int Priotite = -1;
        for (ElementSelectionnable element : this.planRoulotte.getElementSelectionnables()) {
            if (element.getPolygon().contains(point)) {
                element.contient(point);
            }
        }
    }

    public boolean changeDimEllipse(Mesure hauteur, Mesure largeur, Mesure x, Mesure y) {
        DTOEllipse ellipse = ((Ellipse) objetSelection()).getEllipseDTO();
        Ellipse ellipseChanged = (Ellipse) objetSelection();
        if (ellipse == null)
            return true;

        Mesure hauteurOriginale = new Mesure(0,0,0);
        Mesure largeurOriginale = new Mesure(0,0,0);
        Mesure xOriginale = new Mesure(0,0,0);
        Mesure yOriginale = new Mesure(0,0,0);
        int hauteurBrutePanneau = planRoulotte.getDTOPanneau().getHauteurBrute().getMillimetres();
        int largeurBrutePanneau = planRoulotte.getDTOPanneau().getLargeurBrute().getMillimetres();

        largeurOriginale.setMetrique(ellipse.getLargeur().getMillimetres());
        hauteurOriginale.setMetrique(ellipse.getHauteur().getMillimetres());
        xOriginale.setMetrique((int)ellipse.getEllipse().getX());
        yOriginale.setMetrique((int)ellipse.getEllipse().getY());

        hauteur = (hauteur.getMillimetres() >= 1) ? hauteur : hauteurOriginale;
        largeur = (largeur.getMillimetres() >= 1) ? largeur : largeurOriginale;
        x = (x.getMillimetres() >= 0) ? x: xOriginale;
        y = (y.getMillimetres() >= 0) ? y: yOriginale;


        ellipseChanged.editMesuresEllipse(x, y, largeur, hauteur);

        planRoulotte.invalideLaDisposition();


        return ellipseChanged.getEllipse() == ellipse.getEllipse();
    }

    public void supprimerObjet() {}

    public void sauvegarder(String chemin) {
        ObjectOutputStream oos = null;

        try {
            planRoulotte.setCheminEnregistrement(chemin);
            final FileOutputStream fichier = new FileOutputStream(chemin);
            oos = new ObjectOutputStream(fichier);
            oos.writeObject(planRoulotte);
            oos.flush();
            oos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void charger(String chemin) {
        ObjectInputStream ois = null;
        try {
            FileInputStream in = new FileInputStream(chemin);
            ois = new ObjectInputStream(in);
            PlanRoulotte planTemp = (PlanRoulotte) ois.readObject();
            ois.close();
            this.planRoulotte = planTemp;
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getCheminEnregistrement() {
        return planRoulotte.getCheminEnregistrement();
    }


    public boolean getEstImperiale() {
        return planRoulotte.getEstImperial();
    }

    public void setEstImperiale(boolean EstImperiale) {
        planRoulotte.setEstImperial(EstImperiale);
    }


    public static boolean existeIntersection(Point l1p1, Point l1p2, Point l2p1, Point l2p2) {
        Line2D ligne1 = new Line2D.Double(l1p1, l1p2);
        Line2D ligne2 = new Line2D.Double(l2p1, l2p2);

        return ligne1.intersectsLine(ligne2);
    }

    public ElementSelectionnable objetSelection() {
        for (ElementSelectionnable element : this.planRoulotte.getElementSelectionnables()) {
            if (element.isSelected()) {
                return element;
            }
        }
        // Aucun élément n'est sélectionné
        return null;
    }


    public static Point pointIntersection(Point l1p1, Point l1p2, Point l2p1, Point l2p2) {
        // Si l'intersection n'existe pas
        if (!existeIntersection(l1p1, l1p2, l2p1, l2p2)) {
            return null;
        }

        int a1 = l1p2.y - l1p1.y;
        int b1 = l1p1.x - l1p2.x;
        int c1 = a1 * l1p1.x + b1 * l1p1.y;

        int a2 = l2p2.y - l2p1.y;
        int b2 = l2p1.x - l2p2.x;
        int c2 = a2 * l2p1.x + b2 * l2p1.y;

        int delta = a1 * b2 - a2 * b1;

        if (delta == 0) {return null;}
        else {
            return new Point((b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta);
        }
    }

    public ElementSelectionnable.TypeElement whatElementIsSelected() {
        return planRoulotte.whatElementIsSelected();
    }

    public ElementSelectionnable whatObjectIsSelected() {
        return planRoulotte.whatObjectIsSelected();
    }

    public DTOEllipse getDTOEllipseSelectionne() {
        for (Ellipse ellipse : planRoulotte.getEllipseListe()) {
            if(ellipse.isSelected()){
                return ellipse.getEllipseDTO();
            }
        }
        return new DTOEllipse(
                new Ellipse2D.Double(),
                new Mesure(0,0,0),
                new Mesure(0,0,0),
                new Mesure(0,0,0),
                new Mesure(0,0,0));
    }

    public void setFacteurZoom(double facteurZoom) {
        this.facteurZoom = facteurZoom;
    }

    public double getFacteurZoom() {
        return facteurZoom;
    }

    public DTOPlancher getDTOPlancher() {
        return planRoulotte.getDTOPlancher();
    }
    public DTOPoutreArriere getDTOPoutreArriere() {return planRoulotte.getDTOPoutreArriere();}
    public DTOToit getDTOToit() {return planRoulotte.getDTOToit();}
    public DTOHayon getDTOHayon() {return planRoulotte.getDTOHayon();}
    public DTOMurSeparateur getDTOMurSeparateur() {return planRoulotte.getDTOMurSeparateur();}

    public void changeDimHayon(Mesure epaisseur, Mesure distanceDePoutre, Mesure distanceDuPlancher, Mesure traitDeScie, Mesure rayonCourbure, Double poids) {
        planRoulotte.changeDimHayon(epaisseur, distanceDePoutre, distanceDuPlancher, traitDeScie, rayonCourbure, poids);
    }

    // TODO: Ajouter les 5 prochaines méthodes au UML
    public boolean undo() {
        stateIndex--;
        PlanRoulotte tempPlan = listeStates.get(stateIndex);
        planRoulotte = tempPlan;
        planRoulotte.invalideLaDisposition();
        if(stateIndex <= 0) {
            return false; // c'est le premier élément de la liste donc il n'y a plus de undo après
        }
        else {
            return true; // undo toujours disponible
        }
    }

    public boolean redo() {
        stateIndex++;
        this.planRoulotte = listeStates.get(stateIndex);
        planRoulotte.invalideLaDisposition();
        if (stateIndex < listeStates.size()-1) {
            return true; // redo disponible
        } else {
            return false; // redo non disponible
        }
    }

    public void triggerChangementImminent() {
        PlanRoulotte tempRoulotte = serializeAndRead();
        listeStates.add(stateIndex, tempRoulotte);
        stateIndex++;
    }

    private PlanRoulotte serializeAndRead() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(this.planRoulotte);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            outputStream.close();
            outputStrm.close();
            inputStream.close();
            objectInputStream.close();
            return (PlanRoulotte) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isUndoRedoAvailable(boolean isUndo) {
        if (isUndo) {
            return stateIndex > 0;
        } else {
            return stateIndex <= listeStates.size() - 1;
        }
    }

    public boolean isTranslating() {
        return translating;
    }

    public void setTranslating(boolean translating) {
        this.translating = translating;
    }

    public void setGrille() {
        planRoulotte.setEtatGrille();
    }

    public void setEspacementGrille(Mesure mesure) {
        planRoulotte.setEspacementGrille(mesure);
    }

    public Mesure getEspacementGrille() {
        return planRoulotte.getEspacementGrille();
    }

    public void setElementVisibilite(ElementSelectionnable.TypeElement type) {
        planRoulotte.setElementVisibilite(type);
    }
}


