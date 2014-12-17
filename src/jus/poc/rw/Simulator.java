package jus.poc.rw;

import jus.poc.rw.control.Observator;
import jus.poc.rw.v2.Resourcev2;
import jus.poc.rw.v4.deadlock.Detector;

/**
 * Main class for the Readers/Writers application. This class firstly creates a pool of read/write resources  
 * implementing interface IResource. Then it creates readers and writers operating on these resources.
 * @author P.Morat & F.Boyer
 */
public class Simulator{
	protected static final String OPTIONFILENAME = "option.xml";
	/** the version of the protocole to be used */
	protected static String version;
	/** the number of readers involve in the simulation */
	protected static int nbReaders;
	/** the number of writers involve in the simulation */
	protected static int nbWriters;
	/** the number of resources involve in the simulation */
	protected static int nbResources;
	/** the number of resources used by an actor */
	protected static int nbSelection;
	/** the law for the reader using delay */
	protected static int readerAverageUsingTime;
	protected static int readerDeviationUsingTime;
	/** the law for the reader vacation delay */
	protected static int readerAverageVacationTime;
	protected static int readerDeviationVacationTime;
	/** the law for the writer using delay */
	protected static int writerAverageUsingTime;
	protected static int writerDeviationUsingTime;
	/** the law for the writer vacation delay */
	protected static int writerAverageVacationTime;
	protected static int writerDeviationVacationTime;
	/** the law for the writer number of iterations */
	protected static int writerAverageIteration;
	protected static int writerDeviationIteration;
	/** the chosen policy for priority */
	protected static String policy;
	/** Nombre de lectures avant de pouvoir effectuer a nouveau une ecriture sur la ressource */
	public final static int NB_READERS=5;
	/**
	 * make a permutation of the array
	 * @param array the array to be mixed
	 */
	protected static void mixe(Object[] array) {
		int i1, i2;
		Object a;
		for(int k = 0; k < 2 * array.length; k++){
			i1 = Aleatory.selection(1, array.length)[0];
			i2 = Aleatory.selection(1, array.length)[0];
			a = array[i1]; array[i1] = array[i2]; array[i2] = a;
		}
	}
	/**
	 * Retreave the parameters of the application.
	 * @param file the final name of the file containing the options. 
	 */
	protected static void init(String file) {
		// retreave the parameters of the application
		final class Properties extends java.util.Properties {
			private static final long serialVersionUID = 1L;
			public int get(String key){return Integer.parseInt(getProperty(key));}
			public Properties(String file) {
				try{
					loadFromXML(ClassLoader.getSystemResourceAsStream(file));
				}catch(Exception e){e.printStackTrace();}			
			}
		}
		Properties option = new Properties("jus/poc/rw/options/"+file);
		version = option.getProperty("version");
		nbReaders = Math.max(0,new Aleatory(option.get("nbAverageReaders"),option.get("nbDeviationReaders")).next());
		nbWriters = Math.max(0,new Aleatory(option.get("nbAverageWriters"),option.get("nbDeviationWriters")).next());
		nbResources = Math.max(0,new Aleatory(option.get("nbAverageResources"),option.get("nbDeviationResources")).next());
		nbSelection = Math.max(0,Math.min(new Aleatory(option.get("nbAverageSelection"),option.get("nbDeviationSelection")).next(),nbResources));
		readerAverageUsingTime = Math.max(0,option.get("readerAverageUsingTime"));
		readerDeviationUsingTime = Math.max(0,option.get("readerDeviationUsingTime"));
		readerAverageVacationTime = Math.max(0,option.get("readerAverageVacationTime"));
		readerDeviationVacationTime = Math.max(0,option.get("readerDeviationVacationTime"));
		writerAverageUsingTime = Math.max(0,option.get("writerAverageUsingTime"));
		writerDeviationUsingTime = Math.max(0,option.get("writerDeviationUsingTime"));
		writerAverageVacationTime = Math.max(0,option.get("writerAverageVacationTime"));
		writerDeviationVacationTime = Math.max(0,option.get("writerDeviationVacationTime"));
		writerAverageIteration = Math.max(0,option.get("writerAverageIteration"));
		writerDeviationIteration = Math.max(0,option.get("writerDeviationIteration"));
		policy = option.getProperty("policy");
	}
	
	/**
	 * Renvoie le String associe a la police
	 * Il n'est pas necessaire de renvoyer un nouveau string base sur policy, comme il est static, il n'y a pas de risques de le modifier ailleurs 
	 */
	public static String getPolicy(){
		return policy;
	}
	
	/**
	 * Renvoie le String associe a la version
	 * Il n'est pas necessaire de renvoyer un nouveau string base sur policy, comme il est static, il n'y a pas de risques de le modifier ailleurs 
	 */
	public static String getVersion(){
		return version;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String... args) throws Exception{
		// set the application parameters
		init((args.length==1)?args[0]:OPTIONFILENAME);

		ResourcePool rp;
		
		// Creation de l'observator
		Observator obs = new Observator(null);
		obs.init(nbReaders+nbWriters, nbResources);
	
		//Dectector créé pour la version 4
		Detector det = new Detector(nbReaders+nbWriters,nbResources);
		
		int i;
		
		if (version.compareTo("v1")==0 || version.compareTo("v2")==0 || version.compareTo("v3")==0) {
			// création du pool de ressources de v1
			rp = new ResourcePool(nbResources, null, obs, "jus.poc.rw."+version+".Resource"+version);		
		}
		else{
			if(version.compareTo("v4")==0){
				rp = new ResourcePool(nbResources, det, obs, "jus.poc.rw."+version+".Resource"+version);
			}
			else throw(new Exception("Erreur version"));
		}

		/**
		 * Initialisation de la simulation
		 */
		// Création des lecteurs et redacteurs
		Actor[] actors = new Actor[nbReaders+nbWriters];
		for(i=0; i<nbReaders; i++){
			actors[i] = new Reader(new Aleatory(readerAverageUsingTime, readerDeviationUsingTime),
						  new Aleatory(readerAverageVacationTime, readerDeviationVacationTime),
						  new Aleatory(0, 0),
						  rp.selection(nbSelection),obs);
		}
		for (i=nbReaders; i<nbReaders+nbWriters; i++) {
			actors[i] = new Writer(new Aleatory(writerAverageUsingTime, writerDeviationUsingTime),
						  new Aleatory(writerAverageVacationTime, writerDeviationVacationTime),
						  new Aleatory(writerAverageIteration, writerDeviationIteration),
						  rp.selection(nbSelection),obs);
		}
		
		// Melange des acteurs
		mixe(actors);

		// On demarre les acteurs
		for (i=0; i<actors.length; i++) {
			actors[i].start();
		}

		System.out.println("Simulation "+version+"\n");
		
		/**
		 * Simulation v1, fin lorsque les redacteurs s'arretent d'ecrire
		 * Simulation v3, cas similaire a v1, on peut placer LOW_WRITE ou HIGH_WRITE dans le fichier xml en fonction de la politique voulue
		 * Simulation v4, detection-guerison des interblocages
		 */
		if (version.compareTo("v1")==0 || version.compareTo("v3")==0 || version.compareTo("v4")==0) {
			// Boucle tant que les redacteurs n'ont pas fini
			int nbRedFini=0;
			while (nbRedFini != nbWriters) {
				nbRedFini=0;
				for (i=0; i<actors.length; i++) {
					if ((actors[i].getClass().getSimpleName().compareTo("Writer")==0) && (!actors[i].isAlive())) {
						nbRedFini++;
					}
				}
			}
			// Arrete les lecteurs
			System.out.println("Les ecrivains ont fini, on arrete les lecteurs suivants:");
			for (i=0; i<actors.length; i++) {
				if (actors[i].getClass().getSimpleName().compareTo("Reader")==0) {
					actors[i].stop();
					System.out.println("\n" +actors[i].ident());
				}
			}
		}

		/**
		 * Simulation v2
		 * Fin de la simulation quand les redacteurs s'arretent
		 * et qu'un nombre minimum de lectures ait suivi chaque ecriture 
		 */
		if (version.compareTo("v2")==0) {
			// Boucle tant que les redacteurs n'ont pas fini d'ecrire
			int nbRedFini=0;
			while (nbRedFini != nbWriters) {
				nbRedFini=0;
				for (i=0; i<actors.length; i++) {
					if ((actors[i].getClass().getSimpleName().compareTo("Writer")==0) && (!actors[i].isAlive())) {
						nbRedFini++;
					}
				}
			}
			
			// Boucle tant qu'un nombre minimum de lectures n'a ete effectue sur une resource apres ecriture
			int nbRessourcesLues=0; // nombres de resources avec le bon de lectures apres ecriture
			while (nbRessourcesLues<nbResources) {
				IResource[] ir = rp.selection(nbResources);
				nbRessourcesLues=0;
				for (i=0; i<nbResources; i++) {
					if (((Resourcev2)ir[i]).getnbLect() >= NB_READERS) {
						nbRessourcesLues++;
					}
				}
			}
			
			// Arrete les Threads Reader (les Writers ont fini d'ecrire
			// et qu'un nombre minimum de lectures ait suivi chaque ecriture ) 
			for (i=0; i<actors.length; i++) {
				if (actors[i].getClass().getSimpleName().compareTo("Reader")==0) {
					actors[i].stop();
					System.out.println("Les ecrivains ont fini, on arrete les lecteurs suivants:" +actors[i].ident());
				}
			}
		}
	}
}
