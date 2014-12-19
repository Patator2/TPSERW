package jus.poc.rw.v4.deadlock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jus.poc.rw.Actor;
import jus.poc.rw.IResource;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

public class Detector implements IDetector {

	//Tableau associant les ressources aux Actor qui l'utlisent. Les ressources sont représentées par leur id.
	private List<Actor>[] resUsed;
	//Tableau renvoyant la ressource que chaque acteur attend, null sinon.
	private IResource[] actAtt;
	
	/**
	 * Constructor
	 * @param nbActors
	 * @param nbResources
	 */
	//Les elements du tableau sont organises pour etre des List<Actor>, on ignore donc le message d'avertissement
	@SuppressWarnings("unchecked")
	public Detector(int nbActors, int nbResources) {
		int i;
		resUsed=new List[nbResources];
		for(i=0;i<nbResources;i++){
			resUsed[i]=new LinkedList<Actor>();
		}
		actAtt=new IResource[nbActors];
	}
	
	public synchronized void freeResource(Actor arg0, IResource arg1) {
		resUsed[arg1.ident()].remove(arg0);
	}

	public synchronized void useResource(Actor arg0, IResource arg1) {
		resUsed[arg1.ident()].add(arg0);
		//La ressource utilisee n'est plus attendue
		actAtt[arg0.ident()]=null;
	}

	public synchronized void waitResource(Actor arg0, IResource arg1) throws DeadLockException {
		actAtt[arg0.ident()]=arg1;
		detectDeadlock(arg0,arg1);	
	}
	
	private void detectDeadlock(Actor arg0, IResource arg1) throws DeadLockException{
		//On regarde si arg0 est accessible depuis la ressource attendue par arg0
		boolean b=actAtteignable(arg0,arg1);
		
		//Si arg0 est accessible depuis arg1, il y a un cycle, et donc une DeadLockException doit etre lancee
		if (b){
			throw (new DeadLockException(arg0,arg1));
		}
	}
	
	//Renvoie vrai si arg0 est atteignable, faux sinon
	private boolean actAtteignable(Actor arg0, IResource arg1){
		//Liste des elements accessibles depuis arg1
		List<Actor> res=new LinkedList<Actor>(resUsed[arg1.ident()]);
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
				IResource resLocal=actAtt[elt.ident()];
				//Si l'actor attend une ressource
				if(resLocal!=null){
					itBis=resUsed[resLocal.ident()].iterator();
					//On parcourt la liste des actors accessibles depuis la ressource attendue par l'element de res vise
					while(itBis.hasNext()){
						ajtPossible=itBis.next();
						if(!res.contains(ajtPossible)){
							res.add(ajtPossible);
							if(ajtPossible==arg0){
								//Si l'element vise est arg0, on arrete l'execution et on renvoie vrai
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}