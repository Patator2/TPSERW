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
	 * mat[nbActors][nbResources]
	 * mat[i][j] = 0 si l'actor i ne demande pas la resource j.
	 * mat[i][j] = 1 si l'actor i attend la resource j.
	 * mat[i][j] = 2 si l'actor i utilise la resource j.
	 */
	protected int mat[][];
	protected int nbResources;
	protected int nbActors;
	
	/**
	 * Constructor
	 * @param nbActors
	 * @param nbResources
	 */
	public Detector(int nbActors, int nbResources) {
		mat = new int[nbActors][nbResources];
		this.nbActors=nbActors;
		this.nbResources=nbResources;
	}
	
	public synchronized void freeResource(Actor arg0, IResource arg1) {
		mat[arg0.ident()][arg1.ident()] = 0;
	}

	public synchronized void useResource(Actor arg0, IResource arg1) {
		mat[arg0.ident()][arg1.ident()] = 2;
	}

	public synchronized void waitResource(Actor arg0, IResource arg1) throws DeadLockException {
		mat[arg0.ident()][arg1.ident()] = 1;
		startDetect(arg0,arg1);	
	}
	
	public void startDetect(Actor arg0, IResource arg1) throws DeadLockException{
				
		//vector des actors utilisant et attendant au moins une ressource
		Vector<Integer> actId = new Vector<Integer>();
		
		for(int i=0; i<nbActors; i++) {
			for(int j=0; j<nbResources; j++) {
				if (mat[i][j]==2) { // l'actor i utilise la ressource j
					boolean useAndWait=false;
					int k=0;
					while (!useAndWait && k<nbResources) {
						if (mat[i][k]==1) {	// l'actor i attend la ressource k
							if (!actId.contains(i)) {
								actId.add(i);
								useAndWait=true;
							}
						}
						k++;
					}
				}
			}
		}
		
		Iterator<Integer> it = actId.iterator();
		Iterator<Integer> it1;
		//hm: ressources attendues par un actor de actId
		//booleen associe: vrai si la ressource est utilisee par un actor de actId, faux sinon
		HashMap<Integer, Boolean> hm = new HashMap<Integer, Boolean>();
		while (it.hasNext()) {
			int a=it.next();
			for (int j=0; j<nbResources; j++) {
				//si la ressource j est attendue par un actor de actId
				if (mat[a][j]==1) {
					it1 = actId.iterator();
					while (it1.hasNext()) {
						int i=it1.next();
						//si la ressource j est utilisee par un actor de actId
						if (mat[i][j]==2) {
							hm.put(j, true);
						}
					}
					//si la resource j attendue n'est pas utilisee, on met false
					if (!hm.containsKey(j)) {
						hm.put(j, false);
						break;
					}
				}
			}
		}
		
		if (!hm.containsValue(false) && !hm.isEmpty()){
			System.out.println("\nDeadlock\n");
			throw (new DeadLockException(arg0,arg1));
		}
	}
}