package jus.poc.rw.v4.deadlock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jus.poc.rw.Actor;
import jus.poc.rw.IResource;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Detector implements IDetector {

	//HashMap associant les ressources aux Actor qui l'utlisent. Les ressources sont représentées par leur id.
	private HashMap<Integer, List<Actor>> hm = new HashMap<Integer, List<Actor>>();
	//Tableau renvoyant l'id de la ressource que chaque acteur attend, -1 sinon.
	private Integer[] actAtt;
	
	/**
	 * Constructor
	 * @param nbActors
	 * @param nbResources
	 */
	public Detector(int nbActors, int nbResources) {
		int i;
		for(i=0;i<nbResources;i++){
			hm.put(i, new LinkedList<Actor>());
		}
		actAtt=new Integer[nbActors];
		for(i=0;i<nbActors;i++){
			actAtt[i]=-1;
		}
	}
	
	public synchronized void freeResource(Actor arg0, IResource arg1) {
		hm.get(arg1.ident()).remove(arg0);
	}

	public synchronized void useResource(Actor arg0, IResource arg1) {
		hm.get(arg1.ident()).add(arg0);
	}

	public synchronized void waitResource(Actor arg0, IResource arg1) throws DeadLockException {
		actAtt[arg0.ident()]=arg1.ident();
		startDetect(arg0,arg1);	
	}
	
	private void startDetect(Actor arg0, IResource arg1) throws DeadLockException{
		//On regarde la liste des acteurs accessibles depuis la ressource attendue par arg0
		List<Actor> l=actAtteignable(actAtt[arg0.ident()]);
		
		//Si arg0 est inclus dans l, il y a un cycle, et donc une DeadLockException doit etre lancee
		if (l.contains(arg0)){
			System.out.println("\nDeadlock\n");
			throw (new DeadLockException(arg0,arg1));
		}
	}
	
	private List<Actor> actAtteignable(Integer idR){
		//Liste des elements accessibles depuis idR
		List<Actor> res=new LinkedList<Actor>(hm.get(idR));
		//cpy permet de parcourir la liste des elements et ajouter les elements necessaires dans res
		List<Actor> cpy=null;
		Iterator<Actor> it;
		Iterator<Actor> itBis;
		Actor elt;
		Actor ajtPossible;
		//Permet de ne pas traiter les premiers elements de la liste deja analyses
		int skipList=0;
		int i;
		//Tant qu'on a ajoute des elements dans la liste resultat, on continue
		while(!res.equals(cpy)){
			cpy=new LinkedList<Actor>(res);
			it=cpy.iterator();
			for(i=0;i<skipList;i++){
				it.next();
			}
			skipList=0;
			//Tant qu'il reste des elements dans cpy, on regarde quels sont les actors associes aux ressources qu'ils attendent
			while (it.hasNext()){
				skipList++;
				elt=it.next();
				int idRlocal=actAtt[elt.ident()];
				//Si l'actor attend une ressource
				if(idRlocal!=-1){
					itBis=hm.get(idRlocal).iterator();
					//On parcourt la liste des actors accessibles depuis la ressource attendue par l'element de res vise
					while(itBis.hasNext()){
						ajtPossible=itBis.next();
						if(!res.contains(ajtPossible)){
							res.add(ajtPossible);
						}
					}
				}
			}
		}
		return res;
	}
}