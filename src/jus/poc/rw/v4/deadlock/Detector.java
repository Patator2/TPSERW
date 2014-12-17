package jus.poc.rw.v4.deadlock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import jus.poc.rw.Actor;
import jus.poc.rw.IResource;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Detector implements IDetector {

	/**
	 * matrice[nbActors][nbResources]
	 * matrice[i][j] = 0 si l'actor i ne demande pas la resource j.
	 * matrice[i][j] = 1 si l'actor i attend la resource j.
	 * matrice[i][j] = 2 si l'actor i utilise la resource j.
	 */
	protected int matrice[][];
	protected int nbResources;
	protected int nbActors;
	
	/**
	 * Constructor
	 * @param nbActors
	 * @param nbResources
	 */
	public Detector(int nbActors, int nbResources) {
		matrice = new int[nbActors][nbResources];
		this.nbActors=nbActors;
		this.nbResources=nbResources;
	}
	
	public synchronized void freeResource(Actor arg0, IResource arg1) {
		matrice[arg0.ident()][arg1.ident()] = 0;
	}

	public synchronized void useResource(Actor arg0, IResource arg1) {
		matrice[arg0.ident()][arg1.ident()] = 2;
	}

	public synchronized void waitResource(Actor arg0, IResource arg1) throws DeadLockException {
		matrice[arg0.ident()][arg1.ident()] = 1;
		startDetect(arg0,arg1);	
	}
	
	public void startDetect(Actor arg0, IResource arg1) throws DeadLockException{
				
		//contient les actors utilisant ET attendant au moins une resource
		Vector<Integer> actorIdents = new Vector<Integer>();
		
		for(int i=0; i<nbActors; i++) {
			for(int j=0; j<nbResources; j++) {
				if (matrice[i][j]==2) { // si l'actor i utilise la resource j
					boolean trouveActorQuiUtiliseEtAttend=false;
					int k=0;
					while (!trouveActorQuiUtiliseEtAttend && k<nbResources) {
						if (matrice[i][k]==1) {	// si l'actor i attend la resource k
							if (!actorIdents.contains(i)) {
								actorIdents.add(i);
								trouveActorQuiUtiliseEtAttend=true;
							}
						}
						k++;
					}
				}
			}
		}
		
		Iterator<Integer> it = actorIdents.iterator();
		//hm contient chaque resource attendue par un actor du Vector
		//et on lui associe un booleen qui permet de savoir si
		//la resource attendue est utilisee par un actor du Vector
		HashMap<Integer, Boolean> hm = new HashMap<Integer, Boolean>();
		while (it.hasNext()) {
			int a=it.next();
			for (int j=0; j<nbResources; j++) {
				//si la resource est attendue par un actor du Vector
				if (matrice[a][j]==1) {
					Iterator<Integer> it1 = actorIdents.iterator();
					while (it1.hasNext()) {
						int i=it1.next();
						//si la resource attendue par un actor du Vector est
						//utilisee par un actor du Vector
						if (matrice[i][j]==2) {
							hm.put(j, true);
						}
					}
					//si la resource attendue n'est pas utilisee, on met false
					if (!hm.containsKey(j)) { hm.put(j, false); }
				}
			}
		}
		
		if (!hm.containsValue(false) && !hm.isEmpty()){
			System.out.println("\nProblem : DeadLock !\n");
			throw (new DeadLockException(arg0,arg1));
		}
	}
}