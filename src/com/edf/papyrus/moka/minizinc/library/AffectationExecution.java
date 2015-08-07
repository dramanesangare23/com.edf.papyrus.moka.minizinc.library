/*
 * Documentation de la classe "OptimisationExecution" 
 * Dans cette classe, nous allons gérer le comportement de l'élément de notre bibliothèque (MinizincLibrary) appelé
 * Affectation. Cette classe étend la classe 
 * "org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.OpaqueBehaviorExecution", ce qui est recommendé
 * par les développeurs de Papyrus/MOKA de CEA. La recommendation exige d'étendre toujours la dernière classe qui a été 
 * implementée et qui appartient à la même famille de classe que la notre pour être conforme au standard fUML.
 * La méthode à redéfinir est doAction. C'est cette dernière qui s'occupe du comportement de "Affectation" en évaluant 
 * les entrées et en alimentant les sorties.
 * Puisque dans notre modèle UML nous avons deux éléments (deux InputPin) à l'entrée de "Affectation" qui proviennent de
 * la collecte des données des véhicules et du calcul du besoin énergetique et un élément à sa sortie qui contiendra les 
 * affectations véhiculeé/Tournée, nous n'allons donc considérer que les deux premiers éléments de la liste répresentant 
 * les entrées et seul le premier de la liste répresentant les sorties dans la liste des arguments de la méthode 
 * "doAction". 
 * Evidemment en entrée, un élément va répresenter les données des véhicules et l'autre le besoin énergetique tandisqu'en
 * sortie le seul élément va répresenter les affectations Véhicule/Tournée
 * 
 */
package com.edf.papyrus.moka.minizinc.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.StringValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.OpaqueBehaviorExecution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;

public class AffectationExecution extends OpaqueBehaviorExecution {

	//Workspace, path that contains the executable script 
	//that script called "executerMinizinc" arranges different commands necessary
	//for the execution of the minizinc file, minizinc executbale file must be in this path
	public static String CHEMIN = "";

	/* (non-Javadoc)
	 * @see org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.OpaqueBehaviorExecution#doBody(java.util.List, java.util.List)
	 */
	@Override
	public void doBody(List<ParameterValue> inputParameters, List<ParameterValue> outputParameters) {
		if(outputParameters.size() != 0){ // we test if the action has at least one output pin 
			Value inValue = new StringValue();
			((StringValue)inValue).value = "";
			if(inputParameters.size() != 0 ){ // we test if there is at least one input pin
				try{
					//La variable qui contiendra la ligne courante lors de la lecture du fichier de resultat de l'execution du code minizinc (resultat.txt)
					String uneLigne = new String();
					//Le nom du fichier exécutable qui ordonne les commandes nécessaires à l'exécution du code minizinc
					String fichierExecutable = new String();
					//Le nom du fichier de destination des résultats l'exécution du code minizinc
					String fichierResultat = new String();
					//Le tableau de string qui contiendra les paramètres de la fonction de lancement de la ligne de commande
					String commande[] = null;
					//La preparation de l'exécution du code minizinc via l'invite de commande
					//Le fichier executerMinizinc.bat se chargera de mettre en ordre les commandes nécessaires à l'exécution du code minizinc
					ProcessBuilder pb = null; 
					//Le processus qui contiendra le retour l'exécution du thread pb.  ce preocessus nous permettra de faire une pause pour que l'exécution se passe sans
					//problème afin de pouvoir récupérer le resultat
					Process p;

					//Debut creation de fichier
					//On teste d'abord l'os du machine d'exécution
					String os = System.getProperty("os.name");
					if(os.contains("Windows") || os.contains("windows")){
						//Si on a une machine windows, alors on crée un fichier .bat (que l'on va exécuter en ligne de commande cmd)
						try {
							//Le résultat est envoyé dans le fichier resultat.txt
							CHEMIN = "C:\\Users\\G10076\\Documents\\GestionFlotte\\GestionFlotte\\ModelsStage\\minizinc\\";
							fichierExecutable = CHEMIN+"executerMinizinc.bat";
							BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fichierExecutable)));
							writer.write("\n");
							writer.write("@echo off");
							writer.write("::execute .mzn file with command minizinc\n");
							writer.write("echo Cette ligne est a supprimer si l'installation de minizinc "
									+ "est definitif, decommenter la ligne suivante > resultat.txt");
							//sans oublier d'ajouter la localisation du logiciel minizinc aux variables
							//d'environnement de Windows
//							writer.write("minizinc sudoku.mzn sudoku.dzn > resultat.txt");
							writer.write("\n");
							writer.close();
							//Et on prepare la commande à exécuter
							commande = new String[3];
							commande[0] = new String("cmd.exe");
							commande[1] = new String("/C");
							commande[2] = new String(fichierExecutable);
						} catch (IOException ioe){
							System.out.println(ioe.toString());
						}
					} else if (os.contains("Linux") || os.contains("linux") || os.contains("Unix") || os.contains("unix")){
						//Si on a une machine Linux, alors on crée un fichier script shell (que l'on va exécuter avec 
						//l'interpreteur bash)
//						CHEMIN = "/home/dramane/workspace/GestionFlotte/com.edf.papyrus.moka.minizinc.library/lib";
						CHEMIN = "/home/dramane/workspace/GestionFlotte/ModelsStage/minizinc/";
						try {
							//Le resultat est envoye dans le fichier resultat.txt
							fichierExecutable = CHEMIN+"executerMinizinc";
							BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fichierExecutable)));
							writer.write("#bin/sh\n");
							writer.write("#Before all, make sure that mzn2fzn and flatzinc are executable programs.\n");
							writer.write("#To do that, just add the directory containing these commands in the PATH variables that way :\n");
							writer.write("export PATH=$PATH:"+CHEMIN+"bin \n\n");
							writer.write("#execute .mzn file with command minizinc\n");
							writer.write("minizinc affectations.mzn entrees.dzn > resultat.txt"); 	
							writer.write("\n\n exit 0\n");
							writer.close();
							//Il faut maintenant rendre executable le fichier script shell (sans oublier d'ajouter sa
							//localisation au PATH ?)
							try {
								Runtime.getRuntime().exec("chmod +x "+fichierExecutable);
							} catch (IOException e) {
								e.printStackTrace();
							}
							//Et on prepare la commande à exécuter, ici on n'a besoin que d'un parametre dans la commande, c-a-d
							//le nom du fichier a executer
							commande = new String[1];
							commande[0] = new String(fichierExecutable);
						} catch (IOException ioe){
							System.out.println(ioe.toString());
						}	
					}
					//Fin creation de fichier

					//Exécution de la commande
					pb = new ProcessBuilder(commande);
					//L'exécution du code minizinc
					pb.directory(new File(CHEMIN));
					p = pb.start();
					p.waitFor();
					//Apres exécution de minizinc, on recupere le résultat dans un fichier 
					fichierResultat = CHEMIN+"resultat.txt";
					BufferedReader output = new BufferedReader(new InputStreamReader(
							new FileInputStream(fichierResultat)));

					//Récupération du résultat ligne par ligne
					while((uneLigne = output.readLine()) != null){
						((StringValue)inValue).value += uneLigne+"\n"; 
					}

					//On ferme le buffer et on supprime les fichiers generes (executable et le résultat)
					output.close();
					//(new File(fichierResultat)).delete();
					//(new File(fichierExecutable)).delete();
				} catch(IOException e1){
					((StringValue)inValue).value = e1.getMessage();
				} catch (InterruptedException e2) {
					((StringValue)inValue).value = e2.getMessage();
				}
			} else{
				((StringValue)inValue).value = "La valeur par defaut à la sortie de l'action d'appel de resultat du code "
						+ "minizinc";
			}
			//Et enfin les resultats seront enregistres dans le pin de sortie de l'OpaqueBehavior, c'est a dire dans le pin 
			//de CallBehaviorAction qui lui fait reference.
			outputParameters.get(0).values.add(inValue);
		} else {//If the action has no output pin, we do nothing
		}
	}

	@Override
	public Value new_() {
		// TODO Auto-generated method stub
		return new AffectationExecution();
	}

}
