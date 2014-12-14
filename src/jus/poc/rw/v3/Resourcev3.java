package jus.poc.rw.v3;

import jus.poc.rw.Actor;
import jus.poc.rw.Resource;
import jus.poc.rw.Simulator;
import jus.poc.rw.control.IObservator;
import jus.poc.rw.deadlock.DeadLockException;
import jus.poc.rw.deadlock.IDetector;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Resourcev3 extends Resource {
	
	protected Lock l=new ReentrantLock();
	protected final Condition cR=l.newCondition();
	//Nombre de redacteurs en attente
	protected int nbWAtt=0;
			
	/**
	 * Constructor
	 * @param IDetector det
	 * @param IObservator obs
	 */
	public Resourcev3(IDetector det, IObservator obs) {
		super(det, obs);
	}

	/**
	 * L'Actor act commence a lire
	 */
	public void beginR(Actor act) throws InterruptedException, DeadLockException {
		 l.lock();
		 while (Simulator.getPolicy().compareTo("HIGH_WRITE")==0 && nbWAtt>0){
		       cR.await();
		 }
		 System.out.println("Le lecteur " +act.ident()+ " lit la ressource " +this.ident());
		 super.observator.acquireResource(act, this);
	}

	/**
	 * L'Actor act commence a ecrire
	 */
	public void beginW(Actor act) throws InterruptedException, DeadLockException {
		nbWAtt++;
		l.lock();
		nbWAtt--;
		System.out.println("Le redacteur " +act.ident()+ " ecrit dans la ressource " +this.ident());
		super.observator.acquireResource(act, this);
	}

	/**
	 * L'Actor act s'arrete de lire
	 */
	public void endR(Actor act) throws InterruptedException {
		l.unlock();
		System.out.println("Le lecteur " +act.ident()+ " arrete de lire la ressource " +this.ident());
	}

	/**
	 * L'Actor act s'arrete d'ecrire
	 */
	public void endW(Actor act) throws InterruptedException {
		cR.signal();
		l.unlock();
		System.out.println("Le redacteur " +act.ident()+ " arrete d'ecrire dans la ressource " +this.ident());
	}

	public void init(Object act) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Methode impossible pour le moment");
	}

}
